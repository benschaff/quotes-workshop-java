import play.PlayJava

organization := "com.stw"

name := "quotes-workshop-java"

version := "1.0.0"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "requirejs" % "2.1.14-3",
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "angularjs" % "1.2.25",
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.webjars" % "angular-loading-bar" % "0.5.1"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)