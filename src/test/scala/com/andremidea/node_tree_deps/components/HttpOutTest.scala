package com.andremidea.node_tree_deps.components

import com.andremidea.node_tree_deps.schemata._
import org.scalatest.{EitherValues, FlatSpec, Matchers}
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http._
import com.twitter.util.{Await, Future}
import org.scalamock.scalatest.MockFactory
import io.circe.syntax._

class HttpOutTest extends FlatSpec with Matchers with MockFactory with EitherValues {

  it should "return proper package" in {
    val serviceMock = mock[Service[Request, Response]]

    val expected = RegistryPackageVersion(
      "express",
      "Fast, unopinionated, minimalist web framework",
      "4.17.1",
      "http://expressjs.com/",
      VersionRepository("git+https://github.com/expressjs/express.git", "git"),
      Map(
        "body-parser" -> "1.19.0",
        "etag"        -> "~1.8.1",
      ),
      Map.empty,
      Map.empty,
      Some(VersionAuthor("TJ Holowaychuk", Some("tj@vision-media.ca"), None)),
      "MIT",
      "express@4.17.1",
      VersionDist(
        "4491fc38605cf51f8629d39c2b5d026f98a4c134",
        "https://registry.npmjs.org/express/-/express-4.17.1.tgz"
      ),
      "6.4.1",
      VersionNpmUser("dougwilson", "doug@somethingdoug.com")
    )

    val response = Response()
    response.setContentString(expected.asJson.toString())

    (serviceMock.apply _)
      .expects(*)
      .returning(Future(response))

    val result = Await.result(HttpOut(serviceMock).getPackage("express", "latest").value)

    result.right.value should be(expected)
  }

  "get package" should "fail if bad response returned" in {
    val serviceMock = mock[Service[Request, Response]]

    val response = Response(Status.BadRequest)
    response.setContentString("This request will fail")

    (serviceMock.apply _)
      .expects(*)
      .returning(Future(response))

    val result = Await.result(HttpOut(serviceMock).getPackage("express", "latest").value)

    result.left.value.getMessage should be("Bad Response status: 400 body: This request will fail")
  }

  "get package" should "fail if invalid JSON returned" in {
    val serviceMock = mock[Service[Request, Response]]

    val response = Response()
    response.setContentString("{\"invalid\": ,,, \"json\"}")

    (serviceMock.apply _)
      .expects(*)
      .returning(Future(response))

    val result = Await.result(HttpOut(serviceMock).getPackage("express", "latest").value)

    result.left.value.getMessage should be("Failed to parse response to JSON")
  }

  "get package" should "unexpected JSON returned" in {
    val serviceMock = mock[Service[Request, Response]]

    val response = Response()
    response.setContentString("{\"foo\": \"bar\"}")

    (serviceMock.apply _)
      .expects(*)
      .returning(Future(response))

    val result = Await.result(HttpOut(serviceMock).getPackage("express", "latest").value)

    result.left.value.getMessage should be("Failed to parse JSON to expected representation: DecodingFailure(Attempt to decode value on failed cursor, List(DownField(name)))")
  }
}
