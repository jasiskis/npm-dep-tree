package com.andremidea.node_tree_deps

import cats.data.EitherT
import com.andremidea.node_tree_deps.models.PackageWithDeps
import com.andremidea.node_tree_deps.schemata.DependencyNode
import com.twitter.util.Future

object Adapters {
  def fromWire(pack: schemata.RegistryPackageVersion): PackageWithDeps = {
    val deps = pack.dependencies.map {
      case (name: String, version: String) => PackageWithDeps(name, defineVersion(version), false)
    }.toSet
    PackageWithDeps(pack.name, defineVersion(pack.version), true, dependencies = deps)
  }

  def defineVersion(version: String): String = {
    if (Seq(">=", ">", "|", " - ", "<", "<=").forall(!version.contains(_)))
      version.replace("^", "").replace("~", "")
    else
      "latest"
  }

  def toWire(pack: models.PackageWithDeps): schemata.DependencyNode = {

    if (pack.dependencies.isEmpty)
      DependencyNode(name = pack.name, version = pack.version, dependencies = Set.empty)
    else
      DependencyNode(name = pack.name, version = pack.version, dependencies = pack.dependencies.map(toWire))

  }

}
