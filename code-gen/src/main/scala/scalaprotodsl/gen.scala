package scalaprotodsl

import protocbridge.Artifact
import protocbridge.SandboxedJvmGenerator
import scalapb.GeneratorOption

object gen:
  def apply(options: GeneratorOption*): (SandboxedJvmGenerator, Seq[String]) =
    (
      SandboxedJvmGenerator.forModule(
        "scala",
        Artifact(
          BuildInfo.organization,
          BuildInfo.artifact,
          BuildInfo.version
        ),
        "scalaprotodsl.CodeGenerator$",
        CodeGenerator.suggestedDependencies
      ),
      options.map(_.toString)
    )

  def apply(options: Set[GeneratorOption] = Set.empty): (SandboxedJvmGenerator, Seq[String]) =
    apply(options.toSeq*)
