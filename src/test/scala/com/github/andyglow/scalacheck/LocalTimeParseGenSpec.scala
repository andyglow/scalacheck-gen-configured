package com.github.andyglow.scalacheck

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}

import org.scalatest.Matchers._
import org.scalatest._


class LocalTimeParseGenSpec extends WordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "LocalTime" should {

      val d0 = LocalTime.of(8, 0, 0)
      val d1 = LocalTime.of(12, 12, 0)
      val d2 = LocalTime.of(18, 30, 25)

      "handle const" in {
        doGen[LocalTime]("const: 15").value shouldBe LocalDateTime.ofInstant(Instant.ofEpochMilli(15), Tempo.systemZoneOffset).toLocalTime
        doGen[LocalTime]("const: 08:00").value shouldBe d0
      }

      "handle range" in {
        doGen[LocalTime]("range: 08:00 .. 18:30:25").value should (be >= d0 and be <= d2)
      }

      "handle oneof" in {
        doGen[LocalTime]("oneof: 08:00, 12:12, 18:30:25").value should (be (d0) or (be (d1) or be (d2)))
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
        s"not handle $dfn" in { doGen[LocalTime](dfn) shouldBe 'left }
    }
  }
}

