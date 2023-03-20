val Scala3 = "3.8.4"

val ScalaPB = "1.0.0-alpha.5"

val Protobuf = "4.36.0-RC1"

crossPaths := false

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "io.github.ntnj"

scalaVersion := Scala3

PB.protocVersion := Protobuf

lazy val codeGen = project
  .in(file("code-gen"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "codegen",
    scalacOptions ++= Seq("-deprecation"),
    buildInfoKeys := Seq[BuildInfoKey](
      "organization"    -> sys.env.getOrElse("GROUP", organization.value),
      "artifact" -> sys.env.getOrElse("ARTIFACT", name.value),
      "version"  -> sys.env.getOrElse("VERSION", version.value)
    ),
    buildInfoPackage := "scalaprotodsl",
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "compilerplugin" % ScalaPB,
      "com.thesamet.scalapb" %% "scalapb-runtime" % ScalaPB,
      "com.google.protobuf" % "protobuf-java" % Protobuf
    )
  )

// NOTE: the `protoc-gen-scala-proto-compat` executable project used the `sbt-protoc-gen-project`
// plugin (protocGenProject), which has no sbt 2 build yet, so it is dropped for now. The e2e
// tests below generate code via sbt-protoc's LocalCodeGenPlugin, independent of that executable.

lazy val e2e = project
  .in(file("e2e"))
  .enablePlugins(LocalCodeGenPlugin)
  .settings(
    publish / skip := true,
    codeGenClasspath := (codeGen / Compile / fullClasspath).value,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.3.3" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / PB.targets := Seq(
      genModule("scalaprotodsl.CodeGenerator$") -> (Compile / sourceManaged).value,
      PB.gens.java(Protobuf) -> (Compile / sourceManaged).value
    )
  )

lazy val root: Project =
  project
    .in(file("."))
    .settings(publish / skip := true)
    .aggregate(codeGen)
