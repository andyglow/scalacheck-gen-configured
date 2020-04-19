package com.github.andyglow.scalacheck

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec


class LongMakeGenSpec extends AnyWordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "Long" should {

      "handle posNum" in { doGen[Long]("posNum").value should be >= 0L }

      "handle negNum" in { doGen[Long]("negNum").value should be <= 0L }

      "handle const" in {
        doGen[Long]("const: 15").value shouldBe 15L
        doGen[Long]("const: -15").value shouldBe -15L
      }

      "handle range" in {
        doGen[Long]("range: 5..7").value should (be >= 5L and be <= 7L)
      }

      "handle oneof" in {
        doGen[Long]("oneof: 1,2,3").value should (be >= 1L and be <= 3L)
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
        s"not handle $dfn" in { doGen[Long](dfn) shouldBe Symbol("left") }
    }
  }
}

