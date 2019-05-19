package ch.frankel.blog.eps

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File
import java.nio.file.Paths
import javax.annotation.Generated


@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
class GenerateMojo : AbstractMojo() {

    private val packageName = "ch.frankel.blog.eps"
    private val className = "Reflective"

    @Parameter(defaultValue = "\${project.build.directory}", readonly = true)
    private lateinit var buildDirectory: File

    override fun execute() {
        val root = prepareFilesystem(buildDirectory)
        val stopWordsSpec = FunSpec.builder("stopWords")
            .addCode("""return read("stop_words.txt").flatMap { it.split(",") }""")
            .build()
        val extractWordsSpec = FunSpec.builder("extractWords")
            .addParameter(ParameterSpec.builder("filename", String::class).build())
            .addCode(
                """
            |return read(filename)
            |.flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
            |.filter { it.isNotBlank() && it.length >= 2 }
            |""".trimMargin()
            ).build()
        val frequenciesSpec = FunSpec.builder("frequencies")
            .addParameter(
                ParameterSpec.builder(
                    "words",
                    List::class.asTypeName().parameterizedBy(String::class.asTypeName())
                ).build()
            ).addCode(
                """
            |return words.groupBy { it }
            |.map { it.key to it.value.size }
            |.sortedBy { it.second }
            |.takeLast(25)
            |.toMap()
            |""".trimMargin()
            ).build()
        val file = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.classBuilder(ClassName(packageName, className))
                    .addAnnotation(AnnotationSpec.builder(Generated::class)
                        .addMember(CodeBlock.of("value = [\"Exercises in Programming Style Generate Plugin\"]"))
                        .build())
                    .addFunction(stopWordsSpec)
                    .addFunction(extractWordsSpec)
                    .addFunction(frequenciesSpec)
                    .build()
            )
            .build()
        file.writeTo(root)
    }

    private fun prepareFilesystem(buildDirectory: File): File {
        val subPath = Paths.get("generated-sources", "kotlin")
        return buildDirectory.resolve(subPath.toFile()).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
}