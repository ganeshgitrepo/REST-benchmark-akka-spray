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
  "cc.spray" %% "spray-http" % "0.7.0" % "compile" withSources(),
  "cc.spray" %% "spray-server" % "0.7.0" % "compile" withSources(),
  "cc.spray.json" %% "spray-json" % "1.0" % "compile" withSources(),
  "se.scalablesolutions.akka" % "akka-actor" % "1.1.3",
  "se.scalablesolutions.akka" % "akka-http" % "1.1.3",
  "org.eclipse.jetty" % "jetty-webapp" % "8.0.0.M3" % "jetty"
)
