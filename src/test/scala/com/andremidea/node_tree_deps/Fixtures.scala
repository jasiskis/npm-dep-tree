package com.andremidea.node_tree_deps

import com.andremidea.node_tree_deps.schemata.{
  RegistryPackageVersion,
  VersionAuthor,
  VersionDist,
  VersionNpmUser,
  VersionRepository
}

object Fixtures {

  def response(name: String, version: String, dependencies: Map[String, String] = Map.empty): RegistryPackageVersion = {
    RegistryPackageVersion(
      name,
      name,
      version,
      s"http://$name.com/",
      VersionRepository("git+https://github.com/expressjs/express.git", "git"),
      dependencies,
      Map.empty,
      Map.empty,
      Some(VersionAuthor("test", Some("test"), None)),
      "MIT",
      "test",
      VersionDist(
        "123",
        "https://registry.npmjs.org/bla/bla"
      ),
      "6.4.1",
      VersionNpmUser("test", "test@test.com")
    )
  }

}
