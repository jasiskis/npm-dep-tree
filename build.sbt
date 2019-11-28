import Dependencies._

ThisBuild / scalaVersion     := "2.12.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

lazy val root = (project in file("."))
  .settings(
    name := "npm-dep-tree",
    libraryDependencies := Seq(scalaTest % Test,
      finchCirce,
      circeParser,
      circeGeneric,
      circeGenericExtras,
      twitterServer,
      scalaLogging,
      logback,
      scalaMock,
      scalaGraph,
      pprint % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
