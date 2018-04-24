package arrow.ap.tests

import com.google.common.collect.ImmutableList
import com.google.common.io.Files
import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File
import java.nio.file.Paths

abstract class APTest(
  val pckg: String
) : StringSpec() {

  fun testProcessor(
    vararg processor: AnnotationProcessor
  ) {

    processor.forEach { (name, sources, dest, proc, error) ->

      val parent = File(".").absoluteFile.parent

      val stubs = Paths.get(parent, "models", "build", "tmp", "kapt3", "stubs", "main", *pckg.split(".").toTypedArray()).toFile()
      val expectedDir = Paths.get("", "src", "test", "resources", *pckg.split(".").toTypedArray()).toFile()

      if (dest == null && error == null) {
        throw Exception("Destination file and error cannot be both null")
      }

      if (dest != null && error != null) {
        throw Exception("Destination file or error must be set")
      }

      name {

        val temp = Files.createTempDir()

        val compilation = javac()
          .withProcessors(proc)
          .withOptions(ImmutableList.of("-Akapt.kotlin.generated=$temp", "-proc:only"))
          .compile(sources.map {
            val stub = File(stubs, it).toURI().toURL()
            JavaFileObjects.forResource(stub)
          })

        if (error != null) {

          assertThat(compilation)
            .failed()
          assertThat(compilation)
            .hadErrorContaining(error)

        } else {

          assertThat(compilation)
            .succeeded()

          temp.listFiles().size shouldBe 1

          val expected = File(expectedDir, dest).readText()
          val actual = temp.listFiles()[0].readText()

          actual shouldBe expected

        }
      }

    }

  }

}
