val Scala3 = "3.3.0-RC3"

val Scala212 = "2.12.17"

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "io.github.ntnj"

ThisBuild / scalaVersion := Scala3

lazy val codeGen = (projectMatrix in file("code-gen"))
  .enablePlugins(BuildInfoPlugin)
  .defaultAxes()
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](version, scalaVersion, sbtVersion),
    buildInfoPackage := "scalaprotocompat",
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "compilerplugin" % scalapb.compiler.Version.scalapbVersion,
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion
    )
  )
  .jvmPlatform(scalaVersions = Seq(Scala212, Scala3))

lazy val codeGenJVM212 = codeGen.jvm(Scala212)

lazy val protocGenScalaProtoCompat = protocGenProject("protoc-gen-scala-proto-compat", codeGenJVM212)
  .settings(
    Compile / mainClass := Some("scalaprotocompat.CodeGenerator"),
    scalaVersion := Scala212
  )

lazy val e2e = (projectMatrix in file("e2e"))
  .enablePlugins(LocalCodeGenPlugin)
  .defaultAxes()
  .settings(
    publish / skip := true,
    codeGenClasspath := (codeGenJVM212 / Compile / fullClasspath).value,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0-M7" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / PB.targets := Seq(
      genModule("scalaprotocompat.CodeGenerator$") -> (Compile / sourceManaged).value,
      // scalaprotocompat.CodeGenerator -> (Compile / sourceManaged).value,
      // PB.gens.kotlin -> (Compile / sourceManaged).value,
      PB.gens.java -> (Compile / sourceManaged).value
    )
  )
  .jvmPlatform(scalaVersions = Seq(Scala3))

lazy val root: Project =
  project
    .in(file("."))
    .settings(
      publishArtifact := false,
      publish := {},
      publishLocal := {}
    )
    .aggregate(protocGenScalaProtoCompat.agg)
    .aggregate(codeGen.projectRefs: _*)
