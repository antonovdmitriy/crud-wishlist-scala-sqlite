ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "crud-wishlist-scala-sqlite"
  )

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.5.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.15"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"


//libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-http" % "10.5.0",
//  "com.typesafe.akka" %% "akka-stream" % "2.7.0",
//  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",
//  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0",
//  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0" % Test,
//  "com.typesafe.akka" %% "akka-testkit" % "2.7.0"
////  "org.scalatest" %% "scalatest" % "3.2.9" % Test,
////  "org.scalatest" %% "scalatest-flatspec" % "3.2.10" % Test
//)
