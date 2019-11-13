package com.andremidea.node_tree_deps.components.graph

import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}

trait PackageRepository {
  def retrieveConnectedPackages(node: PackageVersion): Option[PackageWithDeps]
}
