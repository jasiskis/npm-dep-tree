package com.andremidea.node_tree_deps
import com.andremidea.node_tree_deps.components.HttpOut
import com.andremidea.node_tree_deps.components.graph.InMemoryGraph
import com.andremidea.node_tree_deps.schemata.DependencyNode
import com.twitter.finagle.Http
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.circe.generic.auto._

object Main extends TwitterServer {

  val inMemoryGraph  = InMemoryGraph()
  val httpOut = HttpOut(Http.client.withTls("registry.npmjs.org").newService(s"registry.npmjs.org:443"))

  val getTree: Endpoint[DependencyNode] =
    get("tree" :: path[String] :: path[String]) { (name: String, version: String) =>
      Ok(Controller.getDependencyTree(name, version, inMemoryGraph, inMemoryGraph, httpOut))
    }

  def main(): Unit = {
    val server = Http.server
      .configured(Stats(statsReceiver))
      .serve(":8081", getTree.toService)

    onExit { server.close() }

    Await.ready(adminHttpServer)
  }
}
