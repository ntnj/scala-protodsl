package sbtprotocgenproject

import sbt.*
import sbt.Keys.*
import sbt.internal.util.Attributed
import sbtprotoc.ProtocPlugin
import sbtprotoc.ProtocPlugin.autoImport.PB
import protocbridge.{Artifact, SandboxedJvmGenerator}
import xsbti.HashedVirtualFileRef

/** Minimal sbt 2 reimplementation of the parts of `sbt-protoc-gen-project` that this build uses: `genModule`, `codeGenClasspath`, and the `LocalCodeGenPlugin`
  * that wires a locally-compiled ScalaPB code generator into sbt-protoc.
  *
  * The upstream plugin (`com.thesamet %% sbt-protoc-gen-project`) has no sbt 2 build yet, so this reproduces just what the build needs. The `protocGenProject`
  * DSL (for a standalone protoc-gen executable) is intentionally not reproduced — that project was dropped in the sbt 2 migration.
  */
object LocalCodeGenPlugin extends AutoPlugin:
  override def requires: Plugins = sbt.plugins.JvmPlugin && ProtocPlugin
  override def trigger: PluginTrigger = noTrigger

  // Sentinel artifact whose "resolution" returns the local code generator's
  // classpath instead of downloading anything (see artifactResolver below).
  private[sbtprotocgenproject] val DummyArtifact: Artifact =
    Artifact("scalapb-local-codegen", "scalapb-local-codegen", "0.0.0")

  object autoImport:
    val codeGenClasspath = taskKey[Seq[Attributed[HashedVirtualFileRef]]](
      "Classpath containing the locally-compiled ScalaPB code generator"
    )

    def genModule(name: String): SandboxedJvmGenerator =
      SandboxedJvmGenerator.forModule("codegen", DummyArtifact, name, Nil)

  import autoImport.*

  override def projectSettings: Seq[Setting[?]] = Seq(
    // The generator lives in a sibling project that changes as we edit it, so
    // always regenerate. (sbt 2 invalidates classloaders on classpath changes,
    // so the old PB.cacheClassLoaders := false is no longer needed.)
    Compile / PB.recompile := true,
    // Resolves to real files, so it can't be a cached task (sbt 2 caches by default).
    Compile / PB.artifactResolver := Def.uncached:
      val converter = fileConverter.value
      // sbt 2 classpaths are virtualized; map them back to real files for protoc-bridge.
      val classpath = (Compile / codeGenClasspath).value.map(a => converter.toPath(a.data).toFile)
      val base = (Compile / PB.artifactResolver).value
      (artifact: Artifact) => if artifact == DummyArtifact then classpath else base(artifact)
  )
