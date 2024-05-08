ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.0"

lazy val root = (project in file("."))
  .settings(
    name := "kafka-perf",
    libraryDependencies ++= {

      val zioVersion = "2.0.22"

      Seq(
        // ZIO
        "dev.zio" %% "zio-streams" % zioVersion,
        "dev.zio" %% "zio-kafka" % "2.7.4",

        "io.monix" %% "monix" % "3.4.1"
      )
    }
  )
