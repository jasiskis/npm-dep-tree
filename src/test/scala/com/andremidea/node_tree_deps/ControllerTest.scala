package com.andremidea.node_tree_deps

import cats.data.EitherT
import com.andremidea.node_tree_deps.components.HttpOut
import com.andremidea.node_tree_deps.components.graph.{GraphStore, PackageRepository}
import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}
import com.twitter.util.Future
import io.catbird.util._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class ControllerTest extends FlatSpec with Matchers with MockFactory {

  "upsertPackage" should "fetch and upsert the package into storage" in {
    val httpOut = mock[HttpOut]
    val storage = mock[GraphStore[PackageVersion]]

    val response = Fixtures.response("express", "4.17.1", Map("body-parser" -> "1.19.0", "etag" -> "1.8.1"))

    (httpOut.getPackage _)
      .expects("express", "4.17.1")
      .returning(EitherT.right(Future(response)))

    val expected = PackageWithDeps(
      "express",
      "4.17.1",
      true,
      Set(
        PackageWithDeps("body-parser", "1.19.0", false, Set()),
        PackageWithDeps("etag", "1.8.1", false, Set())
      )
    )
    (storage.upsert _)
      .expects(expected.toPackageVersion, Set(PackageVersion("body-parser", "1.19.0"), PackageVersion("etag", "1.8.1")))

    Controller.upsertPackage(expected, storage, httpOut) should be(expected)
  }

  "upsertPackage" should "upsert twice if version is latest" in {
    val httpOut = mock[HttpOut]
    val storage = mock[GraphStore[PackageVersion]]

    val response = Fixtures.response("express", "4.17.1", Map("body-parser" -> "1.19.0", "etag" -> "1.8.1"))

    val fromRequest = PackageWithDeps(
      "express",
      "latest",
      false
    )

    (httpOut.getPackage _)
      .expects(fromRequest.name, fromRequest.version)
      .returning(EitherT.right(Future(response)))

    val expected = PackageWithDeps(
      "express",
      "4.17.1",
      true,
      Set(
        PackageWithDeps("body-parser", "1.19.0", false, Set()),
        PackageWithDeps("etag", "1.8.1", false, Set())
      )
    )
    (storage.upsert _)
      .expects(expected.toPackageVersion.copy(version = "latest"),
               Set(PackageVersion("body-parser", "1.19.0"), PackageVersion("etag", "1.8.1")))

    (storage.upsert _)
      .expects(expected.toPackageVersion, Set(PackageVersion("body-parser", "1.19.0"), PackageVersion("etag", "1.8.1")))

    Controller.upsertPackage(fromRequest, storage, httpOut) should be(expected)
  }

  "fetchDependencyTree" should "return cached nodes" in {
    val httpOut    = mock[HttpOut]
    val storage    = mock[GraphStore[PackageVersion]]
    val repository = mock[PackageRepository]

    val fromRequest = PackageVersion(
      "express",
      "4.17.1"
    )

    val expected = PackageWithDeps(
      "express",
      "4.17.1",
      true,
      Set(
        PackageWithDeps("body-parser", "1.19.0", true, Set()),
        PackageWithDeps("etag", "1.8.1", true, Set())
      )
    )

    (repository.retrieveConnectedPackages _)
      .expects(fromRequest)
      .returning(Some(expected))

    Controller.fetchDepencencyTree(fromRequest, storage, repository, httpOut) should be(expected)
  }

  "fetchDependencyTree" should "fetch remaining dependencies" in {
    val httpOut    = mock[HttpOut]
    val storage    = mock[GraphStore[PackageVersion]]
    val repository = mock[PackageRepository]

    val fromRequest = PackageVersion(
      "express",
      "4.17.1"
    )

    val etag       = PackageWithDeps("etag", "1.8.1", true, Set())
    val bodyParser = PackageWithDeps("body-parser", "1.19.0", true, Set())
    val expected = PackageWithDeps(
      "express",
      "4.17.1",
      true,
      Set(
        bodyParser,
        etag
      )
    )

    val cached = PackageWithDeps(
      "express",
      "4.17.1",
      true,
      Set(
        bodyParser,
        etag.copy(dependenciesFetched = false)
      )
    )

    // retrieving express
    (repository.retrieveConnectedPackages _)
      .expects(fromRequest)
      .returning(Some(cached))

    // retrieving etag
    (repository.retrieveConnectedPackages _)
      .expects(etag.toPackageVersion)
      .returning(Some(etag))

    Controller.fetchDepencencyTree(fromRequest, storage, repository, httpOut) should be(expected)
  }

  "fetchDependencyTree" should "fetch non-cached dependencies" in {
    val httpOut    = mock[HttpOut]
    val storage    = mock[GraphStore[PackageVersion]]
    val repository = mock[PackageRepository]

    val fromRequest = PackageVersion(
      "express",
      "4.17.1"
    )
    val etag       = PackageWithDeps("etag", "1.8.1", true, Set())
    val bodyParser = PackageWithDeps("body-parser", "1.19.0", true, Set())
    val expected = PackageWithDeps(
      "express",
      "4.17.1",
      true,
      Set(
        bodyParser,
        etag
      )
    )
    val cached = PackageWithDeps(
      "express",
      "4.17.1",
      true,
      Set(
        bodyParser,
        etag.copy(dependenciesFetched = false)
      )
    )

    val response = Fixtures.response("etag", "1.8.1")

    // retrieving express
    (repository.retrieveConnectedPackages _)
      .expects(fromRequest)
      .returning(Some(cached))

    // retrieving non-cached etag
    (repository.retrieveConnectedPackages _)
      .expects(etag.toPackageVersion)
      .returning(Some(etag.copy(dependenciesFetched = false)))

    // fetching etag from npm registry
    (httpOut.getPackage _)
      .expects("etag", "1.8.1")
      .returning(EitherT.right(Future(response)))

    // upserting into cache
    (storage.upsert _)
      .expects(etag.toPackageVersion, Set.empty[PackageVersion])

    Controller.fetchDepencencyTree(fromRequest, storage, repository, httpOut) should be(expected)
  }

  "fetchDependencyTree" should "fetch recursive dependencies" in {
    val httpOut    = mock[HttpOut]
    val storage    = mock[GraphStore[PackageVersion]]
    val repository = mock[PackageRepository]

    val packageResp  = Fixtures.response("package", "0.1", Map("package2" -> "0.2"))
    val package2Resp = Fixtures.response("package2", "0.2", Map("package3" -> "0.3"))
    val package3Resp = Fixtures.response("package3", "0.3", Map.empty)

    val pack = PackageVersion("package", "0.1")
    // retrieving package
    (repository.retrieveConnectedPackages _)
      .expects(pack)
      .returning(None)

    // fetching package
    (httpOut.getPackage _)
      .expects("package", "0.1")
      .returning(EitherT.right(Future(packageResp)))

    // upserting package into cache
    (storage.upsert _)
      .expects(PackageVersion("package", "0.1"), Set(PackageVersion("package2", "0.2")))

    // retrieving package2
    val package2 = PackageVersion("package2", "0.2")
    (repository.retrieveConnectedPackages _)
      .expects(package2)
      .returning(Some(package2.toWithDeps(false)))

    // fetching package2
    (httpOut.getPackage _)
      .expects("package2", "0.2")
      .returning(EitherT.right(Future(package2Resp)))

    // upserting package2 into cache
    (storage.upsert _)
      .expects(package2, Set(PackageVersion("package3", "0.3")))

    // retrieving package3
    val package3 = PackageVersion("package3", "0.3")
    (repository.retrieveConnectedPackages _)
      .expects(package3)
      .returning(Some(package3.toWithDeps(false)))

    // fetching package3
    (httpOut.getPackage _)
      .expects("package3", "0.3")
      .returning(EitherT.right(Future(package3Resp)))

    // upserting package3 into cache
    (storage.upsert _)
      .expects(package3, Set.empty[PackageVersion])

    val expected = pack
      .toWithDeps(true)
      .copy(
        dependencies = Set(
          package2
            .toWithDeps(true)
            .copy(dependencies = Set(package3.toWithDeps(true)))))

    Controller.fetchDepencencyTree(pack, storage, repository, httpOut) should be(expected)
  }

}
