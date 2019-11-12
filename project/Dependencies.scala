import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  lazy val finchCirce = "com.github.finagle" %% "finch-circe" % "0.31.0"
  lazy val circeParser = "io.circe" %% "circe-parser" % "0.12.3"
  lazy val circeGeneric = "io.circe" %% "circe-generic" % "0.12.3"
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % "0.12.2"
  lazy val pprint = "com.lihaoyi" %% "pprint" % "0.5.6"
  lazy val twitterServer = "com.twitter" %% "twitter-server" % "19.11.0"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  lazy val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0" % Test
  lazy val scalaGraph = "org.scala-graph" % "graph-core_2.12" % "1.13.0"


}
