package scalaprotocompat

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse
import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.Descriptors._
import protocbridge.Artifact
import protocgen.{CodeGenApp, CodeGenResponse, CodeGenRequest}
import scalapb.compiler.{DescriptorImplicits, FunctionalPrinter, ProtobufGenerator}
import scalapb.options.compiler.Scalapb
import scala.collection.JavaConverters._

object CodeGenerator extends CodeGenApp {
  override def registerExtensions(registry: ExtensionRegistry): Unit = {
    Scalapb.registerAllExtensions(registry)
  }

  // This is called by CodeGenApp after the request is parsed.
  def process(request: CodeGenRequest): CodeGenResponse =
    ProtobufGenerator.parseParameters(request.parameter) match {
      case Right(params) =>
        val implicits =
          DescriptorImplicits.fromCodeGenRequest(params, request)

        CodeGenResponse.succeed(
          request.filesToGenerate.map(file => new FilePrinter(request.asProto.getCompilerVersion, file, implicits).result),
          Set(CodeGeneratorResponse.Feature.FEATURE_PROTO3_OPTIONAL)
        )
      case Left(error) =>
        CodeGenResponse.fail(error)
    }
}
