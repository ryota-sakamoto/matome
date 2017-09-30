name := """matome"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
val elasticSearchVersion = "5.4.3"

scalaVersion := "2.12.3"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
    jdbc,
    ehcache,
    ws,
    evolutions,
    guice,
    "mysql" % "mysql-connector-java" % "5.1.30",
    "com.typesafe.play" %% "anorm" % "2.6.0-M1",
    "com.typesafe.play" %% "play-mailer" % "6.0.1",
    "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
    "com.typesafe.play" %% "play-json" % "2.6.0",
    "org.elasticsearch" % "elasticsearch" % elasticSearchVersion,
    "com.sksamuel.elastic4s" %% "elastic4s-core" % elasticSearchVersion,
    "com.sksamuel.elastic4s" %% "elastic4s-http" % elasticSearchVersion,
    "org.jsoup" % "jsoup" % "1.10.3",
    "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B4",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)