resolvers ++= Seq(
  "Web Plugin Repository" at "http://siasia.github.com/maven2",
  "Typesafe Repository" at "http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"
)

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % ("0.1.0-"+v))
