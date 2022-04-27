package io.github.quatresh

import com.squareup.kotlinpoet.*
import de.jensklingenberg.mpapt.common.canonicalFilePath
import de.jensklingenberg.mpapt.model.*
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.containingPackage
import org.jetbrains.kotlin.platform.TargetPlatform
import java.io.File

class PartialAnnotationProcessor : AbstractProcessor() {

    private val annotationFqdn = "io.github.quatresh.annotations.Partial"
    private var partialClasses: List<ClassDescriptor> = emptyList()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(annotationFqdn)

    override fun isTargetPlatformSupported(platform: TargetPlatform): Boolean = true

    override fun process(roundEnvironment: RoundEnvironment) {
        partialClasses = roundEnvironment.getElementsAnnotatedWith(annotationFqdn)
            .filterIsInstance<Element.ClassElement>()
            .filter { it.classDescriptor.isData }
            .map { it.classDescriptor }
    }

    override fun processingOver() {
        partialClasses.forEach { classDescriptor ->
            val baseClassConstructorParameters = classDescriptor.constructors.first()
                .getFunctionParameters()
            val partialClassConstructorParameters = baseClassConstructorParameters
                .map { param ->
                    ParameterSpec
                        .builder(
                            param.parameterName,
                            ClassName(param.packagee.packagename, param.packagee.classname)
                                .copy(nullable = true)
                        )
                        .build()
                }

            val partialClassProperties = baseClassConstructorParameters
                .map { param ->
                    PropertySpec
                        .builder(
                            param.parameterName, ClassName(param.packagee.packagename, param.packagee.classname)
                                .copy(nullable = true)
                        )
                        .initializer(param.parameterName)
                        .build()
                }

            val partialClassName = "${classDescriptor.name.asString()}Partial"
            val partialClassFile = "${classDescriptor.canonicalFilePath()?.substringBeforeLast("/")}/$partialClassName.kt"
            val partialClassBuilder = TypeSpec.classBuilder(partialClassName)
                .addModifiers(KModifier.DATA)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameters(partialClassConstructorParameters)
                        .build()
                )
                .addProperties(partialClassProperties)

            FileSpec
                .builder(
                    classDescriptor.containingPackage().toString(),
                    partialClassName
                )
                .generateComments()
                .addType(partialClassBuilder.build())
                .build()
                .toString()
                .replace("public", "")
                .also {
                    File(partialClassFile)
                        .writeText(it)
                }
        }
    }

    private fun FunctionDescriptor.getFunctionParameters(): List<FunctionParameter> {
        return if (valueParameters.isNotEmpty()) {
            this.valueParameters.map { parameter ->
                val fullPackage = parameter.toString()
                    .substringAfter(": ")
                    .substringBefore(" defined")
                    .substringBefore(" /* =")
                    .substringBefore("=")
                    .trim()
                FunctionParameter(
                    parameter.name.asString(),
                    parameter.type.toString().endsWith("?"),
                    Package(
                        fullPackage.split(".").last().replace("?", ""),
                        fullPackage.split(".").dropLast(1).joinToString(".")
                    )
                )
            }.toList()
        } else {
            emptyList()
        }
    }

    private fun FileSpec.Builder.generateComments() = addFileComment(
        """
        | - WARNING -
        | This code is generated AND MUST NOT BE EDITED
    """.trimMargin()
    )
}
