package com.github.andyglow.scalacheck

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}

import matchers.should.Matchers._
import org.scalatest._
import org.scalatest.matchers
import org.scalatest.wordspec.AnyWordSpec


class LocalDateTimeMakeGenSpec extends AnyWordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "LocalDateTime" should {

      val d0 = LocalDateTime.of(2011, 12, 3, 0, 0, 0)
      val d1 = LocalDateTime.of(2011, 12, 4, 0, 0, 0)
      val d2 = LocalDateTime.of(2011, 12, 5, 0, 0, 0)

      "handle const" in {
        doGen[LocalDateTime]("const: 15").value shouldBe LocalDateTime.ofInstant(Instant.ofEpochMilli(15), Tempo.systemZoneOffset)
        doGen[LocalDateTime]("const: 2011-12-03T10:15:30").value shouldBe LocalDateTime.of(LocalDate.of(2011, 12, 3), LocalTime.of(10, 15, 30))
      }

      "handle range" in {
        doGen[LocalDateTime]("range: 2011-12-03T00:00:00 .. 2011-12-04T00:00:00").value should (be >= d0 and be <= d1)
      }

      "handle oneof" in {
        doGen[LocalDateTime]("oneof: 2011-12-03T00:00:00, 2011-12-04T00:00:00, 2011-12-05T00:00:00").value should (be (d0) or (be (d1) or be (d2)))
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
        s"not handle $dfn" in { doGen[LocalDateTime](dfn) shouldBe 'left }
    }
  }
}

