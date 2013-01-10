name := "REST Akka-Spray"

version := "1.1"

organization := "Xebia"

scalaVersion := "2.10.0"

scalacOptions := Seq("-unchecked", "-deprecation")

seq(webSettings :_*)

resolvers ++= Seq(
  "Sonatype Repository" at "http://oss.sonatype.org/content/repositories/releases",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository" at "http://repo.spray.io"
)

libraryDependencies ++= Seq(
  "io.spray" % "spray-servlet" % "1.1-M7",
  "io.spray" % "spray-routing" % "1.1-M7",
  "io.spray" % "spray-testkit" % "1.1-M7" % "test",
  "io.spray" %% "spray-json" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.1.0",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
  "org.specs2" %% "specs2" % "1.12.3" % "test",
  "org.mongodb" %% "casbah-core" % "2.5.0-SNAPSHOT"
)
