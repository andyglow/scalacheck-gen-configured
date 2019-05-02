package com.github.andyglow.scalacheck

import org.scalatest.Matchers._
import org.scalatest._


class IntParseGenSpec extends WordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "Int" should {

      "handle posNum" in { doGen[Int]("posNum").value should be >= 0 }

      "handle negNum" in { doGen[Int]("negNum").value should be <= 0 }

      "handle const" in {
        doGen[Int]("const: 15").value shouldBe 15
        doGen[Int]("const: -15").value shouldBe -15
      }

      "handle range" in {
        doGen[Int]("range: 5..7").value should (be >= 5 and be <= 7)
      }

      "handle oneof" in {
        doGen[Int]("oneof: 1,2,3").value should (be >= 1 and be <= 3)
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
        s"not handle $dfn" in { doGen[Int](dfn) shouldBe 'left }
    }
  }
}

