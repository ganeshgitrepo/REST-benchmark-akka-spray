name := "REST Akka-Spray"

version := "1.0"

organization := "Xebia"

scalaVersion := "2.9.0-1"

seq(webSettings :_*)

resolvers ++= Seq(
  ScalaToolsReleases,
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "cc.spray" %% "spray-http" % "0.7.0" % "compile" withSources(),
  "cc.spray" %% "spray-server" % "0.7.0" % "compile" withSources(),
  "cc.spray.json" %% "spray-json" % "1.0" % "compile" withSources(),
  "se.scalablesolutions.akka" % "akka-actor" % "1.1.3",
  "se.scalablesolutions.akka" % "akka-http" % "1.1.3",
  "se.scalablesolutions.akka" % "akka-testkit" % "1.1.3",
  "org.eclipse.jetty" % "jetty-webapp" % "8.0.0.M3" % "container",
  "org.scala-tools.testing" %% "specs" % "1.6.8" % "test",
  "net.liftweb" %% "lift-json-ext" % "2.4",
  "net.liftweb" %% "lift-json" % "2.4",
  "com.mongodb.casbah" %% "casbah" % "2.1.5.0"
)
