package com.andremidea.node_tree_deps

import scala.util.hashing.MurmurHash3

/* internal models */
object models {

  /* graph node */
  case class PackageVersion(name: String, version: String) {
    def toWithDeps(dependenciesFetched: Boolean): PackageWithDeps = {
      PackageWithDeps(this.name, this.version, dependenciesFetched)
    }
  }

  case class PackageWithDeps(
      name: String,
      version: String,
      dependenciesFetched: Boolean,
      dependencies: Set[PackageWithDeps] = Set.empty) {
    def toPackageVersion: PackageVersion = {
      PackageVersion(this.name, this.version)
    }

    def downstreamDependenciesFetched: Boolean = {
      dependenciesFetched && dependencies.forall(_.downstreamDependenciesFetched)
    }

  }

}
