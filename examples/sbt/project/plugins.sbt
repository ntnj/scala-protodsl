addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.1.0-RC1")

// Run `codeGen/publishLocal` in the parent build first.
// libraryDependencies += "com.github.ntnj.scala-protodsl" %% "codegen" % "0.1.0-SNAPSHOT"
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "com.github.ntnj" % "scala-protodsl" % "master-SNAPSHOT"
