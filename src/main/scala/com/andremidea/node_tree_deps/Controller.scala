package com.andremidea.node_tree_deps

import com.andremidea.node_tree_deps.components.HttpOut
import com.andremidea.node_tree_deps.components.graph.{GraphStore, PackageRepository}
import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}
import com.andremidea.node_tree_deps.schemata.DependencyNode
import com.twitter.conversions.DurationOps._
import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.LazyLogging

object Controller extends LazyLogging {

  def getDependencyTree(name: String,
                        version: String,
                        storage: GraphStore[PackageVersion],
                        packageRepository: PackageRepository,
                        httpOut: HttpOut): DependencyNode = {
    val pack = PackageVersion(name, version)
    Adapters.toWire(fetchDepencencyTree(pack, storage, packageRepository, httpOut))
  }

  def fetchDepencencyTree(pack: PackageVersion,
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
        val fetched = upsertPackage(pack.toWithDeps(false), storage, httpOut)
        val fetchedDependencies = fetched.dependencies.map(dep =>
          fetchDepencencyTree(dep.toPackageVersion, storage, packageRepository, httpOut))
        fetched.copy(dependencies = fetchedDependencies)
      }
    }
  }

  def upsertPackage(packWithDeps: PackageWithDeps,
                    storage: GraphStore[PackageVersion],
                    httpOut: HttpOut): PackageWithDeps = {
    val pack = fetchHttp(packWithDeps.name, packWithDeps.version, httpOut)
    if (packWithDeps.version == "latest")
      // insert duplicated to work around "caching" when searching for a non
      // exact version. Ideally the "latest" would have a expiration
      storage.upsert(pack.copy(version = "latest").toPackageVersion, pack.dependencies.map(_.toPackageVersion))

    storage.upsert(pack.toPackageVersion, pack.dependencies.map(_.toPackageVersion))
    pack
  }

  def fetchHttp(name: String, version: String, httpOut: HttpOut): PackageWithDeps = {
    logger.info(s"http-out fetching package: $name:$version")
    val request: Future[Either[Exception, schemata.RegistryPackageVersion]] =
      httpOut.getPackage(name, version).value

    Await.result(request, 5.seconds) match {
      case Right(response) => Adapters.fromWire(response)
      case Left(ex)        => throw ex
    }
  }
}
