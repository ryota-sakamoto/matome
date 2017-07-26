name := """matome"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    "mysql" % "mysql-connector-java" % "5.1.30",
    "com.typesafe.play" %% "anorm" % "2.5.1",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "com.adrianhurt" %% "play-bootstrap" % "1.2-P25-B3-RC2",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.seleniumhq.selenium" % "selenium-java" % "2.53.1" % "test"
)