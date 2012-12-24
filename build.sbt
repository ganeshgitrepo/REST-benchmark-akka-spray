name := "REST Akka-Spray"

version := "1.1"

organization := "Xebia"

scalaVersion := "2.9.2"

scalacOptions += "-deprecation"

seq(webSettings :_*)

resolvers ++= Seq(
  "Sonatype Repository" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository" at "http://repo.spray.io"
)

libraryDependencies ++= Seq(
  "cc.spray" % "spray-server" % "0.9.0",
  "cc.spray" %% "spray-json" % "1.1.1" cross CrossVersion.full,
  "se.scalablesolutions.akka" % "akka-actor" % "1.3.1",
  "se.scalablesolutions.akka" % "akka-http" % "1.3.1",
  "se.scalablesolutions.akka" % "akka-testkit" % "1.3.1",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
  "org.specs2" %% "specs2" % "1.12.3" % "test",
  "net.liftweb" % "lift-json-ext_2.9.0-1" % "2.4",
  "net.liftweb" % "lift-json_2.9.0-1" % "2.4",
  "com.mongodb.casbah" % "casbah_2.9.0-1" % "2.1.5.0"
)
