package com.andremidea.node_tree_deps

/* internal models */
object models {

  case class PackageVersion(name: String, version: String) {
    def toWithDeps: PackageWithDeps = {
      PackageWithDeps(this.name, this.version)
    }
  }

  case class PackageWithDeps(name: String, version: String, dependencies: Set[PackageWithDeps] = Set.empty)

}
