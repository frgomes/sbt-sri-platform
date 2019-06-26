name := "sbt-sri-platform"

scalaVersion := "2.12.8"

sbtPlugin := true

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions"
)

//bintray
resolvers += Resolver.jcenterRepo

organization := "scalajs-react-interface"

licenses += ("Apache-2.0", url(
  "https://www.apache.org/licenses/LICENSE-2.0.html"))

bintrayOrganization := Some("scalajs-react-interface")

bintrayRepository := "sbt-plugins"

publishArtifact in Test := false

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.0.0-M8")

//Test
libraryDependencies += "com.lihaoyi" %% "utest" % "0.7.1" % Test
testFrameworks += TestFramework("utest.runner.Framework")