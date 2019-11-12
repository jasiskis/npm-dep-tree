package com.andremidea.node_tree_deps

import io.circe._
import io.circe.parser._
import cats.syntax.either._
import io.circe.Decoder.Result
import org.scalatest.{EitherValues, FlatSpec, Matchers}
import schemata._

import scala.io.Source

class SchemataTest extends FlatSpec with Matchers with EitherValues {

  "parsing" should "work for express example" in {

    val expected = RegistryPackageVersion(
      "express",
      "Fast, unopinionated, minimalist web framework",
      "4.17.1",
      "http://expressjs.com/",
      VersionRepository("git+https://github.com/expressjs/express.git", "git"),
      Map(
        "body-parser"         -> "1.19.0",
        "etag"                -> "~1.8.1",
        "range-parser"        -> "~1.2.1",
        "fresh"               -> "0.5.2",
        "depd"                -> "~1.1.2",
        "array-flatten"       -> "1.1.1",
        "methods"             -> "~1.1.2",
        "encodeurl"           -> "~1.0.2",
        "on-finished"         -> "~2.3.0",
        "cookie-signature"    -> "1.0.6",
        "merge-descriptors"   -> "1.0.1",
        "qs"                  -> "6.7.0",
        "statuses"            -> "~1.5.0",
        "content-disposition" -> "0.5.3",
        "utils-merge"         -> "1.0.1",
        "debug"               -> "2.6.9",
        "setprototypeof"      -> "1.1.1",
        "proxy-addr"          -> "~2.0.5",
        "parseurl"            -> "~1.3.3",
        "escape-html"         -> "~1.0.3",
        "content-type"        -> "~1.0.4",
        "cookie"              -> "0.4.0",
        "accepts"             -> "~1.3.7",
        "safe-buffer"         -> "5.1.2",
        "path-to-regexp"      -> "0.1.7",
        "finalhandler"        -> "~1.1.2",
        "vary"                -> "~1.1.2",
        "type-is"             -> "~1.6.18",
        "serve-static"        -> "1.14.1",
        "send"                -> "0.17.1"
      ),
      Map(
        "connect-redis"   -> "3.4.1",
        "ejs"             -> "2.6.1",
        "cookie-parser"   -> "~1.4.4",
        "eslint"          -> "2.13.1",
        "marked"          -> "0.6.2",
        "multiparty"      -> "4.2.1",
        "hbs"             -> "4.0.4",
        "istanbul"        -> "0.4.5",
        "morgan"          -> "1.9.1",
        "supertest"       -> "3.3.0",
        "mocha"           -> "5.2.0",
        "after"           -> "0.8.2",
        "should"          -> "13.2.3",
        "express-session" -> "1.16.1",
        "vhost"           -> "~3.0.2",
        "pbkdf2-password" -> "1.2.1",
        "cookie-session"  -> "1.3.3",
        "method-override" -> "3.0.0"
      ),
      Map(
        "test-ci"  -> "istanbul cover node_modules/mocha/bin/_mocha --report lcovonly -- --require test/support/env --reporter spec --check-leaks test/ test/acceptance/",
        "test"     -> "mocha --require test/support/env --reporter spec --bail --check-leaks test/ test/acceptance/",
        "test-cov" -> "istanbul cover node_modules/mocha/bin/_mocha -- --require test/support/env --reporter dot --check-leaks test/ test/acceptance/",
        "test-tap" -> "mocha --require test/support/env --reporter tap --check-leaks test/ test/acceptance/",
        "lint"     -> "eslint ."
      ),
      Some(VersionAuthor("TJ Holowaychuk", "tj@vision-media.ca", None)),
      "MIT",
      "express@4.17.1",
      VersionDist(
        "4491fc38605cf51f8629d39c2b5d026f98a4c134",
        "https://registry.npmjs.org/express/-/express-4.17.1.tgz"
      ),
      "6.4.1",
      VersionNpmUser("dougwilson", "doug@somethingdoug.com"),
      List(
        VersionAuthor("dougwilson", "doug@somethingdoug.com", None),
        VersionAuthor("jasnell", "jasnell@gmail.com", None),
        VersionAuthor("mikeal", "mikeal.rogers@gmail.com", None)
      )
    )

    val result: Result[RegistryPackageVersion] = readTestFixture("get-express-latest").as[RegistryPackageVersion]

    result.right.value should be(expected)
  }

  "parsing" should "work for fresh example" in {

    val expected = RegistryPackageVersion(
      "fresh",
      "HTTP response freshness testing",
      "0.5.2",
      "https://github.com/jshttp/fresh#readme",
      VersionRepository("git+https://github.com/jshttp/fresh.git", "git"),
      Map.empty,
      Map(
        "eslint-plugin-promise"  -> "3.5.0",
        "eslint-plugin-markdown" -> "1.0.0-beta.6",
        "eslint-plugin-node"     -> "5.1.1",
        "beautify-benchmark"     -> "0.2.4",
        "eslint"                 -> "3.19.0",
        "eslint-plugin-import"   -> "2.7.0",
        "istanbul"               -> "0.4.5",
        "mocha"                  -> "1.21.5",
        "eslint-config-standard" -> "10.2.1",
        "eslint-plugin-standard" -> "3.0.1",
        "benchmark"              -> "2.1.4"
      ),
      Map(
        "test"        -> "mocha --reporter spec --bail --check-leaks test/",
        "test-travis" -> "istanbul cover node_modules/mocha/bin/_mocha --report lcovonly -- --reporter spec --check-leaks test/",
        "test-cov"    -> "istanbul cover node_modules/mocha/bin/_mocha -- --reporter dot --check-leaks test/",
        "lint"        -> "eslint --plugin markdown --ext js,md .",
        "bench"       -> "node benchmark/index.js"
      ),
      Some(VersionAuthor("TJ Holowaychuk", "tj@vision-media.ca", Some("http://tjholowaychuk.com"))),
      "MIT",
      "fresh@0.5.2",
      VersionDist(
        "3d8cadd90d976569fa835ab1f8e4b23a105605a7",
        "https://registry.npmjs.org/fresh/-/fresh-0.5.2.tgz"
      ),
      "3.10.10",
      VersionNpmUser("dougwilson", "doug@somethingdoug.com"),
      List(VersionAuthor("dougwilson", "doug@somethingdoug.com", None))
    )
    val result: Result[RegistryPackageVersion] = readTestFixture("get-fresh-052").as[RegistryPackageVersion]

    result.right.value should be(expected)
  }

  "parsing" should "work for body-parser latest" in {

    val expected = RegistryPackageVersion(
      "body-parser",
      "Node.js body parsing middleware",
      "1.19.0",
      "https://github.com/expressjs/body-parser#readme",
      VersionRepository("git+https://github.com/expressjs/body-parser.git", "git"),
      Map(
        "depd"         -> "~1.1.2",
        "raw-body"     -> "2.4.0",
        "on-finished"  -> "~2.3.0",
        "qs"           -> "6.7.0",
        "http-errors"  -> "1.7.2",
        "debug"        -> "2.6.9",
        "bytes"        -> "3.1.0",
        "content-type" -> "~1.0.4",
        "iconv-lite"   -> "0.4.24",
        "type-is"      -> "~1.6.17"
      ),
      Map(
        "eslint-plugin-promise"  -> "4.1.1",
        "eslint-plugin-markdown" -> "1.0.0",
        "methods"                -> "1.1.2",
        "eslint-plugin-node"     -> "8.0.1",
        "eslint"                 -> "5.16.0",
        "eslint-plugin-import"   -> "2.17.2",
        "istanbul"               -> "0.4.5",
        "supertest"              -> "4.0.2",
        "mocha"                  -> "6.1.4",
        "eslint-config-standard" -> "12.0.0",
        "eslint-plugin-standard" -> "4.0.0",
        "safe-buffer"            -> "5.1.2"
      ),
      Map(
        "lint"        -> "eslint --plugin markdown --ext js,md .",
        "test"        -> "mocha --require test/support/env --reporter spec --check-leaks --bail test/",
        "test-cov"    -> "istanbul cover node_modules/mocha/bin/_mocha -- --require test/support/env --reporter dot --check-leaks test/",
        "test-travis" -> "istanbul cover node_modules/mocha/bin/_mocha --report lcovonly -- --require test/support/env --reporter spec --check-leaks test/"
      ),
      None,
      "MIT",
      "body-parser@1.19.0",
      VersionDist(
        "96b2709e57c9c4e09a6fd66a8fd979844f69f08a",
        "https://registry.npmjs.org/body-parser/-/body-parser-1.19.0.tgz"
      ),
      "6.4.1",
      VersionNpmUser("dougwilson", "doug@somethingdoug.com"),
      List(VersionAuthor("dougwilson", "doug@somethingdoug.com", None))
    )
    val result: Result[RegistryPackageVersion] = readTestFixture("get-body-parser-latest").as[RegistryPackageVersion]
    pprint.pprintln(result)

    result.right.value should be(expected)
  }

  def readTestFixture(fixture: String): Json = {
    parse(Source.fromResource(s"$fixture.json").getLines().mkString).getOrElse(Json.Null)
  }
}
