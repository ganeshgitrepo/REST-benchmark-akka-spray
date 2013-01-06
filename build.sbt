name := "REST Akka-Spray"

version := "1.1"

organization := "Xebia"

scalaVersion := "2.9.2"

scalacOptions += "-Ydependent-method-types"

seq(webSettings :_*)

resolvers ++= Seq(
  "Sonatype Repository" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository" at "http://repo.spray.io"
)

libraryDependencies ++= Seq(
  "io.spray" % "spray-servlet" % "1.0-M7",
  "io.spray" % "spray-routing" % "1.0-M7",
  "io.spray" % "spray-testkit" % "1.0-M7" % "test",
  "io.spray" %% "spray-json" % "1.2.3" cross CrossVersion.full,
  "com.typesafe.akka" % "akka-actor" % "2.0.5",
  "com.typesafe.akka" % "akka-testkit" % "2.0.5",
  "com.typesafe.akka" % "akka-actor-migration" % "2.0.5",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
  "org.specs2" %% "specs2" % "1.12.3" % "test",
  "net.liftweb" % "lift-json-ext_2.9.0-1" % "2.4",
  "net.liftweb" % "lift-json_2.9.0-1" % "2.4",
  "com.mongodb.casbah" % "casbah_2.9.0-1" % "2.1.5.0"
)
