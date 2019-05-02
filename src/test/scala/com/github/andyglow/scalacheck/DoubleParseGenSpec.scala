package com.github.andyglow.scalacheck

import org.scalatest.Matchers._
import org.scalatest._


class DoubleParseGenSpec extends WordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "Double" should {

      "handle posNum" in { doGen[Double]("posNum").value should be >= .0 }

      "handle negNum" in { doGen[Double]("negNum").value should be <= .0 }

      "handle const" in {
        doGen[Double]("const: 15.658").value shouldBe 15.658
        doGen[Double]("const: -0.0002").value shouldBe -0.0002
      }

      "handle range" in {
        doGen[Double]("range: 0.5..0.7").value should (be >= .5 and be <= .7)
      }

      "handle oneof" in {
        doGen[Double]("oneof: 0.1, 0.2, 0.3").value should (be >= .1 and be <= .3)
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
        s"not handle $dfn" in { doGen[Double](dfn) shouldBe 'left }
    }
  }
}

