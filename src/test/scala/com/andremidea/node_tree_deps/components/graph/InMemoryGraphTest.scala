package com.andremidea.node_tree_deps.components.graph

import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class InMemoryGraphTest extends FlatSpec with Matchers with OptionValues {

  it should "add and retrieve a node" in {
    val inMemoryGraph = InMemoryGraph()
    val pack          = PackageVersion(name = "express", version = "latest")

    inMemoryGraph.upsert(pack, Set.empty)
    inMemoryGraph.getShallowPackage(pack).value should be(pack.toWithDeps(true))
  }

  it should "retrieve dependencies of a node" in {
    val inMemoryGraph = InMemoryGraph()
    val pack          = PackageVersion(name = "express", version = "latest")
    val deps          = Set(PackageVersion(name = "foo", version = "latest"), PackageVersion(name = "bar", version = "latest"))

    inMemoryGraph.upsert(pack, deps)
    inMemoryGraph.retrieveConnectedPackages(pack).value should be(
      PackageWithDeps(pack.name, pack.version, true, deps.map(_.toWithDeps(false))))
  }

  it should "retrieve recursive dependencies of a node" in {
    val inMemoryGraph = InMemoryGraph()

    val express = PackageVersion(name = "express", version = "latest")
    val foo     = PackageVersion(name = "foo", version = "latest")
    val bar     = PackageVersion(name = "bar", version = "latest")
    val foobar  = PackageVersion(name = "foobar", version = "latest")
    val barfoo  = PackageVersion(name = "barfoo", version = "latest")

    val expressDeps = Set(foo, bar)
    val fooDeps     = Set(foobar)
    val barDeps     = Set(barfoo)

    inMemoryGraph.upsert(express, expressDeps)
    inMemoryGraph.upsert(foo, fooDeps)
    inMemoryGraph.upsert(bar, barDeps)

    val expected = PackageWithDeps(
      express.name,
      express.version,
      true,
      Set(foo.toWithDeps(true).copy(dependencies = Set(foobar.toWithDeps(false))),
          bar.toWithDeps(true).copy(dependencies = Set(barfoo.toWithDeps(false))))
    )

    inMemoryGraph.retrieveConnectedPackages(express).value should be(expected)
  }
}
