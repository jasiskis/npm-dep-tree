package com.andremidea.node_tree_deps

import io.circe.generic.JsonCodec, io.circe.syntax._
import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec, JsonKey}
/*
  from-wire and to-wire schemata
 */
object schemata {

  implicit val config: Configuration = Configuration.default.withDefaults

  /*
   Representation of npm package version
   as documented on https://github.com/npm/registry/blob/master/docs/REGISTRY-API.md#version
   */
  @ConfiguredJsonCodec
  case class RegistryPackageVersion(name: String,
                                    description: String,
                                    version: String,
                                    homepage: String,
                                    repository: VersionRepository,
                                    dependencies: Map[String, String] = Map.empty,
                                    devDependencies: Map[String, String] = Map.empty,
                                    scripts: Map[String, String] = Map.empty,
                                    author: Option[VersionAuthor] = None,
                                    license: String,
                                    @JsonKey("_id") id: String,
                                    dist: VersionDist,
                                    @JsonKey("_npmVersion") npmVersion: String,
                                    @JsonKey("_npmUser") npmUser: VersionNpmUser,
                                    maintainers: Seq[VersionAuthor] = Seq.empty)

  @ConfiguredJsonCodec
  case class VersionRepository(url: String, @JsonKey("type") repoType: String)

  @ConfiguredJsonCodec
  case class VersionAuthor(name: String, email: String, url: Option[String] = None)

  @ConfiguredJsonCodec
  case class VersionDist(shasum: String, tarball: String)

  @ConfiguredJsonCodec
  case class VersionNpmUser(name: String, email: String)

  /* to-wire schemata */
  @ConfiguredJsonCodec
  case class DependencyNode(name: String, version: String, dependencies: Set[DependencyNode])

}
