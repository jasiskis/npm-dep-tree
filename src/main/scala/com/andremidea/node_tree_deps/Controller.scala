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

    val withDeps = packageRepository.retrieveConnectedPackages(pack)
    logger.debug(s"retrieved package ${pack} from repository ${withDeps}")

    withDeps match {
      case Some(x) if x.allDependenciesFetched => Adapters.toWire(x)
      case _ => {
        fetchDepencencyTree(pack, storage, packageRepository, httpOut)
        Adapters.toWire(packageRepository.retrieveConnectedPackages(pack).get)
      }
    }
  }

  private def fetchDepencencyTree(pack: PackageVersion,
                                  storage: GraphStore[PackageVersion],
                                  packageRepository: PackageRepository,
                                  httpOut: HttpOut): PackageWithDeps = {

    val fetched = getOrFetch(pack.toWithDeps(false), storage, packageRepository, httpOut)

    if (fetched.allDependenciesFetched) {
      fetched
    } else {
      val bla = fetched.dependencies
        .filterNot(_.allDependenciesFetched)
        .map(x => fetchDepencencyTree(x.toPackageVersion, storage, packageRepository, httpOut))
      fetched.copy(dependencies = bla)
    }
  }

  def getOrFetch(packWithDeps: PackageWithDeps,
                 storage: GraphStore[PackageVersion],
                 packageRepository: PackageRepository,
                 httpOut: HttpOut): PackageWithDeps = {
    packWithDeps.allDependenciesFetched match {
      case true => packWithDeps
      case _ => {
        val pack = fetch(packWithDeps, httpOut)
        if (packWithDeps.version == "latest") {
          // insert duplicated to work around "caching" when searching for a non
          // exact version. Ideally the "latest" would have a expiration
          storage.upsert(pack.copy(version = "latest").toPackageVersion, pack.dependencies.map(_.toPackageVersion))
          storage.upsert(pack.toPackageVersion, pack.dependencies.map(_.toPackageVersion))
        } else
          storage.upsert(pack.toPackageVersion, pack.dependencies.map(_.toPackageVersion))

        pack
      }
    }
  }

  def fetch(pack: PackageWithDeps, httpOut: HttpOut): PackageWithDeps = {
    logger.info(s"Fetching package: ${pack}")
    val x: Future[Either[Exception, schemata.RegistryPackageVersion]] =
      httpOut.getPackage(pack.name, pack.version).value

    Await.result(x) match {
      case Right(y) => Adapters.fromWire(y)
      case Left(ex) => throw ex
    }
  }
}
