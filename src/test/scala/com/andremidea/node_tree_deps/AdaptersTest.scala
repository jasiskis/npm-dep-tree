package com.andremidea.node_tree_deps

import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}
import com.andremidea.node_tree_deps.schemata.DependencyNode
import org.scalatest.{FlatSpec, Matchers}

class AdaptersTest extends FlatSpec with Matchers {

  it should "convert PackageWithDeps to external schema" in {

    val express = PackageVersion(name = "express", version = "latest")
    val foo = PackageVersion(name = "foo", version = "latest")
    val foobar = PackageVersion(name = "foobar", version = "latest")

    val fixture = PackageWithDeps(express.name, express.version, true,
      Set(foo.toWithDeps(false).copy(dependencies = Set(foobar.toWithDeps(false)))))

    val expected = DependencyNode(
      "express",
      "latest",
      Set(DependencyNode("foo", "latest", Set(DependencyNode("foobar", "latest", Set()))))
    )

    Adapters.toWire(fixture) should be (expected)
  }


  it should "parse version approximating to the used" in {
    Adapters.defineVersion("1.2.3") should be ("1.2.3")
    Adapters.defineVersion("^1.2.3") should be ("1.2.3")
    Adapters.defineVersion("~1.2.3") should be ("1.2.3")

    Adapters.defineVersion(">1.2 <3") should be ("latest")
    Adapters.defineVersion("1.2 || 1.3.2") should be ("latest")
    Adapters.defineVersion("1.2 - 1.3") should be ("latest")
  }

}
