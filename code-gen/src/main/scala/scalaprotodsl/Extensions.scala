package scalaprotodsl

import com.google.protobuf.DescriptorProtos.Edition
import com.google.protobuf.DescriptorProtos.FeatureSet
import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf.Descriptors.EnumDescriptor
import com.google.protobuf.Descriptors.FileDescriptor
import com.google.protobuf.ExtensionLite
import com.google.protobuf.JavaFeaturesProto
import com.google.protobuf.JavaFeaturesProto.JavaFeatures
import com.google.protobuf.JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass
import scalapb.compiler.DescriptorImplicits
import scalapb.compiler.NameUtils

class Extensions(using impl: DescriptorImplicits):
  import impl.*

  private val javaFeatures: ExtensionLite[FeatureSet, JavaFeatures] = JavaFeaturesProto.java_
  private val getFeatures =
    val m = Class.forName("com.google.protobuf.Descriptors$GenericDescriptor").getDeclaredMethod("getFeatures")
    m.setAccessible(true)
    m

  private def nestedInFileClass(desc: AnyRef): Boolean =
    getFeatures.invoke(desc).asInstanceOf[FeatureSet].getExtension(javaFeatures).getNestInFileClass == NestInFileClass.YES

  extension (m: Descriptor)
    def javaClassName: String =
      if !m.getFile.isEdition2024 then m.javaTypeName
      else
        m.getContainingType match
          case null => m.getFile.editionClassName(m.getName, nestedInFileClass(m))
          case p    => p.javaClassName + "." + m.getName

  extension (e: EnumDescriptor)
    def javaClassName: String =
      if !e.getFile.isEdition2024 then e.javaTypeName
      else
        e.getContainingType match
          case null => e.getFile.editionClassName(e.getName, nestedInFileClass(e))
          case p    => p.javaClassName + "." + e.getName

  extension (f: FileDescriptor)
    private def isEdition2024: Boolean =
      f.toProto.hasEdition && f.toProto.getEdition.getNumber >= Edition.EDITION_2024.getNumber

    private def editionClassName(name: String, nested: Boolean): String =
      val pkg = f.javaPackageAsSymbol
      val prefix = if pkg.isEmpty then "" else pkg + "."
      if nested then prefix + f.editionOuterClass + "." + name else prefix + name

    private def editionOuterClass: String =
      if f.getOptions.hasJavaOuterClassname then f.getOptions.getJavaOuterClassname
      else
        val base = f.getName.substring(f.getName.lastIndexOf('/') + 1).stripSuffix(".proto")
        NameUtils.snakeCaseToCamelCase(base, true) + "Proto"
