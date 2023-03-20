package scalaprotodsl

import com.google.protobuf.DescriptorProtos.Edition
import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.JavaFeaturesProto
import com.google.protobuf.compiler.PluginProtos.{CodeGeneratorRequest, CodeGeneratorResponse}
import protocbridge.ProtocCodeGenerator
import protocgen.CodeGenRequest
import scalapb.compiler.{DescriptorImplicits, ProtobufGenerator}
import scalapb.options.Scalapb

object CodeGenerator extends ProtocCodeGenerator:
  def run(req: Array[Byte]): Array[Byte] =
    val registry = ExtensionRegistry.newInstance()
    Scalapb.registerAllExtensions(registry)
    JavaFeaturesProto.registerAllExtensions(registry)
    val request = CodeGenRequest(CodeGeneratorRequest.parseFrom(req, registry))
    val response = CodeGeneratorResponse.newBuilder()
    response.setSupportedFeatures(
      (CodeGeneratorResponse.Feature.FEATURE_PROTO3_OPTIONAL.getNumber
        | CodeGeneratorResponse.Feature.FEATURE_SUPPORTS_EDITIONS.getNumber).toLong
    )
    response.setMinimumEdition(Edition.EDITION_PROTO2.getNumber)
    response.setMaximumEdition(Edition.EDITION_2026.getNumber)
    ProtobufGenerator.parseParameters(request.parameter) match
      case Right(params) =>
        val implicits = DescriptorImplicits.fromCodeGenRequest(params, request)
        request.filesToGenerate.foreach(file => response.addFile(new FilePrinter(request.asProto.getCompilerVersion, file, implicits).result))
      case Left(error) =>
        response.setError(error)
    response.build().toByteArray
