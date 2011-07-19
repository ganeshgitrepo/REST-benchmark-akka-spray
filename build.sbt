name := "REST Akka-Spray"

version := "1.0"

organization := "Xebia"

scalaVersion := "2.9.0-1"

seq(webSettings :_*)

resolvers ++= Seq(
  ScalaToolsSnapshots,
  "Akka Repository" at "http://akka.io/repository"
)

libraryDependencies ++= Seq(
  "cc.spray" %% "spray-http" % "0.7.0-SNAPSHOT" % "compile" withSources(),
  "cc.spray" %% "spray-server" % "0.7.0-SNAPSHOT" % "compile" withSources(),
  "cc.spray.json" %% "spray-json" % "1.0" % "compile" withSources(),
  "se.scalablesolutions.akka" % "akka-actor" % "1.1.3",
  "se.scalablesolutions.akka" % "akka-http" % "1.1.3",
  "org.eclipse.jetty" % "jetty-webapp" % "8.0.0.M3" % "jetty",
  "org.scala-tools.testing" %% "specs" % "1.6.8" % "test"
  "net.liftweb" % "lift-json-ext_2.9.0-1" % "2.4-SNAPSHOT",
  "net.liftweb" % "lift-json_2.9.0-1" % "2.4-SNAPSHOT",
  "com.mongodb.casbah" % "casbah_2.9.0-1" % "2.1.5.0"
)

// The snapshots frequently have incorrect checksums; ignore them.
checksums := Nil
