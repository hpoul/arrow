package arrow.ap.tests

import arrow.optics.OpticsProcessor

class IsoTest : APTest("arrow.ap.objects.iso") {

  init {

    testProcessor(AnnotationProcessor(
      name = "Isos cannot be generated for sealed classes",
      sourceFile = "IsoSealed.java",
      errorMessage = """
      |Cannot generate arrow.optics.Iso for arrow.ap.objects.iso.IsoSealed
      |                                        ^
      |  arrow.optics.OpticsTarget.ISO is an invalid @optics argument for arrow.ap.objects.iso.IsoSealed.
      |  It is only valid for data classes.
      """.trimMargin(),
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Isos cannot be generated for huge classes",
      sourceFile = "IsoXXL.java",
      errorMessage = """
      |Cannot generate arrow.optics.Iso for arrow.ap.objects.iso.IsoXXL
      |                                        ^
      |  Iso generation is supported for data classes with up to 22 constructor parameters.
      """.trimMargin(),
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Isos will be generated for data class",
      sourceFile = "Iso.java",
      destFile = "Iso.kt",
      processor = OpticsProcessor()
    ))

  }

}