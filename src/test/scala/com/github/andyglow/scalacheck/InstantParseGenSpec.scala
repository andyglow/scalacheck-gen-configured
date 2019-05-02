package com.github.andyglow.scalacheck

import java.time.{Instant, LocalDateTime}

import org.scalatest.Matchers._
import org.scalatest._


class InstantParseGenSpec extends WordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "Instant" should {

      val d0 = LocalDateTime.of(2011, 12, 3, 0, 0, 0).toInstant(Tempo.systemZoneOffset)
      val d1 = LocalDateTime.of(2011, 12, 3, 10, 15, 30).toInstant(Tempo.systemZoneOffset)
      val d2 = LocalDateTime.of(2011, 12, 4, 0, 0, 0).toInstant(Tempo.systemZoneOffset)
      val d3 = LocalDateTime.of(2011, 12, 5, 0, 0, 0).toInstant(Tempo.systemZoneOffset)

      "handle const" in {
        doGen[Instant]("const: 15").value shouldBe Instant.ofEpochMilli(15)
        doGen[Instant]("const: 2011-12-03T10:15:30").value shouldBe d1
      }

      "handle range" in {
        doGen[Instant]("range: 2011-12-03T00:00:00 .. 2011-12-04T00:00:00").value should (be >= d0 and be <= d2)
      }

      "handle oneof" in {
        doGen[Instant]("oneof: 2011-12-03T00:00:00, 2011-12-04T00:00:00, 2011-12-05T00:00:00").value should (be (d0) or (be (d2) or be (d3)))
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
        s"not handle $dfn" in { doGen[Instant](dfn) shouldBe 'left }
    }
  }
}

