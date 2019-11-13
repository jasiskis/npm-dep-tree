package com.andremidea.node_tree_deps.components.graph

trait GraphStore[T] {
  def upsert(key: T, connections: Set[T]): Unit
}
