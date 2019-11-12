package com.andremidea.node_tree_deps.components.graph

import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}

trait GraphQuery {
  def retrieveConnectedNodes(node: PackageVersion): Option[PackageWithDeps]
  def getShallowNode(node: PackageVersion): Option[PackageWithDeps]
}
