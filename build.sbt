lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := """yagro-coding-challenge""",
    scalaVersion := "3.3.3",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )