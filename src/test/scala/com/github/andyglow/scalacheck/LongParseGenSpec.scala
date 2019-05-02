package com.github.andyglow.scalacheck

import org.scalatest.Matchers._
import org.scalatest._


class LongParseGenSpec extends WordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "Long" should {

      "handle posNum" in { doGen[Long]("posNum").value should be >= 0l }

      "handle negNum" in { doGen[Long]("negNum").value should be <= 0l }

      "handle const" in {
        doGen[Long]("const: 15").value shouldBe 15l
        doGen[Long]("const: -15").value shouldBe -15l
      }

      "handle range" in {
        doGen[Long]("range: 5..7").value should (be >= 5l and be <= 7l)
      }

      "handle oneof" in {
        doGen[Long]("oneof: 1,2,3").value should (be >= 1l and be <= 3l)
      }

      for { dfn <- List(
        "numChar",
        "alphaUpperChar",
        "alphaLowerChar",
        "alphaChar",
        "alphaNumChar",
        "asciiChar",
        "asciiPrintableChar",
        "identifier",
        "numStr",
        "alphaUpperStr",
        "alphaLowerStr",
        "alphaStr",
        "alphaNumStr",
        "asciiStr",
        "asciiPrintableStr") }
        s"not handle $dfn" in { doGen[Long](dfn) shouldBe 'left }
    }
  }
}

