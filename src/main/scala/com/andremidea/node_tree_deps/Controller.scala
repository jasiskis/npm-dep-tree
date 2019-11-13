package com.andremidea.node_tree_deps

import cats.data.EitherT
import com.andremidea.node_tree_deps.components.HttpOut
import com.andremidea.node_tree_deps.components.graph.{PackageRepository, GraphStore}
import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}
import com.andremidea.node_tree_deps.schemata.DependencyNode
import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.LazyLogging
import io.catbird.util._

object Controller extends LazyLogging {

  def getDependencyTree(name: String,
                        version: String,
                        storage: GraphStore[PackageVersion],
                        packageRepository: PackageRepository,
                        httpOut: HttpOut): DependencyNode = {
    val pack = PackageVersion(name, version)
    Adapters.toWire(fetchDepencencyTree(pack, storage, packageRepository, httpOut))
  }

  private def fetchDepencencyTree(pack: PackageVersion,
                                  storage: GraphStore[PackageVersion],
                                  packageRepository: PackageRepository,
                                  httpOut: HttpOut): PackageWithDeps = {

    val cachedPackage = packageRepository.retrieveConnectedPackages(pack)
    logger.debug(s"retrieved package ${pack} from repository ${cachedPackage}")

    cachedPackage match {
      case Some(cached) if cached.downstreamDependenciesFetched => cached
      case Some(cached) if cached.dependenciesFetched => {
        val fetchedDependencies = cached.dependencies
          .map(
            dep =>
              if (dep.downstreamDependenciesFetched) dep
              else fetchDepencencyTree(dep.toPackageVersion, storage, packageRepository, httpOut))

        cached.copy(dependencies = fetchedDependencies)
      }
      case _ => {
        val fetched = fetch(pack.toWithDeps(false), storage, httpOut)
        val fetchedDependencies = fetched.dependencies.map(dep =>
          fetchDepencencyTree(dep.toPackageVersion, storage, packageRepository, httpOut))
        fetched.copy(dependencies = fetchedDependencies)
      }
    }
  }

  def fetch(packWithDeps: PackageWithDeps, storage: GraphStore[PackageVersion], httpOut: HttpOut): PackageWithDeps = {
    val pack = fetchHttp(packWithDeps, httpOut)
    if (packWithDeps.version == "latest")
      // insert duplicated to work around "caching" when searching for a non
      // exact version. Ideally the "latest" would have a expiration
      storage.upsert(pack.copy(version = "latest").toPackageVersion, pack.dependencies.map(_.toPackageVersion))

    storage.upsert(pack.toPackageVersion, pack.dependencies.map(_.toPackageVersion))
    pack
  }

  def fetchHttp(pack: PackageWithDeps, httpOut: HttpOut): PackageWithDeps = {
    logger.info(s"http-out fetching package: ${pack}")
    val x: Future[Either[Exception, schemata.RegistryPackageVersion]] =
      httpOut.getPackage(pack.name, pack.version).value

    Await.result(x) match {
      case Right(y) => Adapters.fromWire(y)
      case Left(ex) => throw ex
    }
  }
}
