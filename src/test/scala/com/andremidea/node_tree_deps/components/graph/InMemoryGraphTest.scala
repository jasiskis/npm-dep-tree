package com.andremidea.node_tree_deps.components.graph

import com.andremidea.node_tree_deps.models.{PackageVersion, PackageWithDeps}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class InMemoryGraphTest extends FlatSpec with Matchers with OptionValues {

  it should "add and retrieve a node" in {
    val inMemoryGraph = InMemoryGraph()
    val pack = PackageVersion(name = "express", version = "latest")

    inMemoryGraph.put(pack, Set.empty)
    inMemoryGraph.getShallowNode(pack).value should be(pack.toWithDeps)
  }

  it should "retrieve dependencies of a node" in {
    val inMemoryGraph = InMemoryGraph()
    val pack = PackageVersion(name = "express", version = "latest")
    val deps = Set(PackageVersion(name = "foo", version = "latest"), PackageVersion(name = "bar", version = "latest"))

    inMemoryGraph.put(pack, deps)
    inMemoryGraph.retrieveConnectedNodes(pack).value should be(PackageWithDeps(pack.name, pack.version, deps.map(_.toWithDeps)))
  }

  it should "retrieve recursive dependencies of a node" in {
    val inMemoryGraph = InMemoryGraph()

    val express = PackageVersion(name = "express", version = "latest")
    val foo = PackageVersion(name = "foo", version = "latest")
    val bar = PackageVersion(name = "bar", version = "latest")
    val foobar = PackageVersion(name = "foobar", version = "latest")
    val barfoo = PackageVersion(name = "barfoo", version = "latest")

    val expressDeps = Set(foo, bar)
    val fooDeps = Set(foobar)
    val barDeps = Set(barfoo)

    inMemoryGraph.put(express, expressDeps)
    inMemoryGraph.put(foo, fooDeps)
    inMemoryGraph.put(bar, barDeps)

    val expected = PackageWithDeps(express.name, express.version,
      Set(foo.toWithDeps.copy(dependencies = Set(foobar.toWithDeps)),
        bar.toWithDeps.copy(dependencies = Set(barfoo.toWithDeps)))
    )

    inMemoryGraph.retrieveConnectedNodes(express).value should be(expected)
  }
}
