import sbt._

object Dependencies {
  val rootDependencies = Seq(

    "com.typesafe" % "config" % "1.3.4",

    "com.typesafe.scala-logging"    %% "scala-logging"       % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",

    "com.github.pathikrit"          %% "better-files"        % "3.8.0",

    "org.clapper" %% "scalasti" % "3.0.1"

  )
}
