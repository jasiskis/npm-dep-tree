package com.andremidea.node_tree_deps.components.graph

import com.andremidea.node_tree_deps.models
import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.GraphPredef._
import scalax.collection.mutable.Graph

case class InMemoryGraph() extends GraphQuery with GraphStore[PackageVersion] {

  private val graph: Graph[PackageVersion, DiEdge] = Graph.empty

  override def retrieveConnectedNodes(node: PackageVersion): Option[PackageWithDeps] = {
    graph.find(node).map { n =>
      val successors: Set[graph.NodeT] = n.diSuccessors

      def getSuccessors(node: graph.NodeT): PackageWithDeps = {
        val nodeValue = node.value
        if (node.hasSuccessors) {
          PackageWithDeps(nodeValue.name, nodeValue.version, node.diSuccessors.map(getSuccessors(_)))
        } else {
          PackageWithDeps(nodeValue.name, nodeValue.version)
        }
      }

      getSuccessors(n)
    }
  }

  override def put(key: PackageVersion, connections: Set[PackageVersion]): Unit = {
    graph += key
    connections.foreach(c => {
      graph += key ~> c
    })
  }

  override def getShallowNode(node: PackageVersion): Option[PackageWithDeps] = {
    graph.find(node).map( x => x.toWithDeps)
  }
}
