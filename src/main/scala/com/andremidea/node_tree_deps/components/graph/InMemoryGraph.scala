package com.andremidea.node_tree_deps.components.graph

import com.andremidea.node_tree_deps.models
import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.GraphPredef._
import scalax.collection.mutable.Graph

case class InMemoryGraph() extends PackageRepository with GraphStore[PackageVersion] {

  // The var is to allow non-key contents in the node that can be updated
  private case class Node(content: PackageVersion)(var dependenciesFetched: Boolean)

  private val graph: Graph[Node, DiEdge] = Graph.empty

  override def retrieveConnectedPackages(key: PackageVersion): Option[PackageWithDeps] = {
    val node = Node(key)(false)
    graph.find(node).map { n =>
      val successors: Set[graph.NodeT] = n.diSuccessors

      def getSuccessors(node: graph.NodeT): PackageWithDeps = {
        val nodeValue = node.value
        if (node.hasSuccessors) {
          PackageWithDeps(nodeValue.content.name, nodeValue.content.version, nodeValue.dependenciesFetched, node.diSuccessors.map(getSuccessors(_)))
        } else {
          PackageWithDeps(nodeValue.content.name, nodeValue.content.version, nodeValue.dependenciesFetched)
        }
      }

      getSuccessors(n)
    }
  }

  override def upsert(key: PackageVersion, connections: Set[PackageVersion]): Unit = {
    val nodeToUpsert = Node(key)(false)

    val existingNode: Node = graph.find(nodeToUpsert) match {
      case Some(n) => n.value
      case _ => {
        graph += nodeToUpsert
        nodeToUpsert
      }
    }

    existingNode.dependenciesFetched = true
    connections.foreach(c => {
      graph += existingNode ~> Node(c)(false)
    })
  }
}
