ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "crud-wishlist-scala-sqlite"
  )

mainClass := Some("com.example.wishlist.App")

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.5.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "test"
)

libraryDependencies += "ch.megard" %% "akka-http-cors" % "1.2.0"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.15" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.41.2.1"
libraryDependencies += "com.zaxxer" % "HikariCP" % "5.0.1"

assemblyMergeStrategy in assembly := {
  case "META-INF/versions/9/module-info.class" =>
    MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

Test / fork := true
