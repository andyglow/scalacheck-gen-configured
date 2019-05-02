package com.github.andyglow.scalacheck

import java.time.{Instant, LocalDate, LocalDateTime}

import org.scalatest.Matchers._
import org.scalatest._


class LocalDateParseGenSpec extends WordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "LocalDate" should {

      val d0 = LocalDate.of(2011, 12, 3)
      val d1 = LocalDate.of(2011, 12, 4)
      val d2 = LocalDate.of(2011, 12, 5)

      "handle const" in {
        doGen[LocalDate]("const: 15").value shouldBe LocalDateTime.ofInstant(Instant.ofEpochMilli(15), Tempo.systemZoneOffset).toLocalDate
        doGen[LocalDate]("const: 2011-12-03").value shouldBe d0
      }

      "handle range" in {
        doGen[LocalDate]("range: 2011-12-03 .. 2011-12-05").value should (be >= d0 and be <= d2)
      }

      "handle oneof" in {
        doGen[LocalDate]("oneof: 2011-12-03, 2011-12-04, 2011-12-05").value should (be (d0) or (be (d1) or be (d2)))
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
        s"not handle $dfn" in { doGen[LocalDate](dfn) shouldBe 'left }
    }
  }
}

