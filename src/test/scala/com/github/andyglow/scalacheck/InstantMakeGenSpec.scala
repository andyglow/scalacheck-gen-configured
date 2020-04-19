package com.github.andyglow.scalacheck

import java.time.Instant

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec


class InstantMakeGenSpec extends AnyWordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "Instant" should {

      val d0 = Instant parse "2011-12-03T00:00:00Z"
      val d1 = Instant parse "2011-12-03T10:15:30Z"
      val d2 = Instant parse "2011-12-04T00:00:00Z"
      val d3 = Instant parse "2011-12-05T00:00:00Z"

      "handle const" in {
        doGen[Instant]("const: 15").value shouldBe Instant.ofEpochMilli(15)
        doGen[Instant]("const: 2011-12-03T10:15:30Z").value shouldBe d1
      }

      "handle range" in {
        doGen[Instant]("range: 2011-12-03T00:00:00Z .. 2011-12-04T00:00:00Z").value should (be >= d0 and be <= d2)
      }

      "handle oneof" in {
        doGen[Instant]("oneof: 2011-12-03T00:00:00Z, 2011-12-04T00:00:00Z, 2011-12-05T00:00:00Z").value should (be (d0) or (be (d2) or be (d3)))
      }

      for { dfn <- List(
        "posNum",
        "negNum",
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
        s"not handle $dfn" in { doGen[Instant](dfn) shouldBe Symbol("left") }
    }
  }
}

