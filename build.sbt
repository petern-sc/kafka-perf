ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.0"

lazy val root = (project in file("."))
  .settings(
    name := "kafka-perf",
    fork := true,
    libraryDependencies ++= {

      val zioVersion = "2.0.22"

      Seq(
        // ZIO
        "dev.zio" %% "zio-streams" % zioVersion,
        "dev.zio" %% "zio-kafka" % "2.7.1",

        "io.monix" %% "monix" % "3.4.1",

        "org.slf4j" % "slf4j-simple" % "2.0.13",
      )
    }
  )
