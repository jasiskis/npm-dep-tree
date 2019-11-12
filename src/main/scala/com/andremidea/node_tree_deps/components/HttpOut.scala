package com.andremidea.node_tree_deps.components

import cats.data.EitherT
import com.andremidea.node_tree_deps.schemata._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.{Await, Future, Try}
import io.catbird.util._
import io.circe.parser._


case class HttpOut(httpService: Service[Request, Response]) {

  def getPackage(name: String, version: String): EitherT[Future, Exception, RegistryPackageVersion] = {
    val request = Request(Method.Get, s"/${name}/${version}")

    doRequest(request)
  }

  private def doRequest(request: Request): EitherT[Future, Exception, RegistryPackageVersion] = {
    val response: Future[Response] = httpService(request)

    for {
      resp <- EitherT.right(response)
      okResp <- EitherT.fromEither[Future](
        resp.status match {
          case Status.Successful(x) => Right(resp)
          case _ => Left(new Exception(s"Bad Response status: ${resp.statusCode} body: ${resp.getContentString()}"))
        }
      )
      body <- EitherT.fromEither[Future](parse(okResp.getContentString()))
          .leftMap(failure => {
            new Exception("Failed to parse response to JSON")
          })
      parsed <- EitherT.fromEither[Future](body.as[RegistryPackageVersion])
          .leftMap(failure => {
            new Exception("Failed to parse JSON to expected representation")
          })
    } yield parsed
  }
}
