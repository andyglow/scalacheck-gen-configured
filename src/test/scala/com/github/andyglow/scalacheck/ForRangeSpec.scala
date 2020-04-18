package com.github.andyglow.scalacheck

import matchers.should.Matchers._
import org.scalatest._
import org.scalatest.matchers
import org.scalatest.wordspec.AnyWordSpec

class ForRangeSpec extends AnyWordSpec {

  "ForRange" should {
    "support Byte" in { "implicitly[ForRange[Byte]]" should compile }
    "support Short" in { "implicitly[ForRange[Short]]" should compile }
    "support Int" in { "implicitly[ForRange[Int]]" should compile }
    "support Long" in { "implicitly[ForRange[Long]]" should compile }
    "support Float" in { "implicitly[ForRange[Float]]" should compile }
    "support Double" in { "implicitly[ForRange[Double]]" should compile }
    "support Char" in { "implicitly[ForRange[Char]]" should compile }
    "support String" in { "implicitly[ForRange[String]]" should compile }
    "support Boolean" in { "implicitly[ForRange[Boolean]]" should compile }
    "support java.util.Date" in { "implicitly[ForRange[java.util.Date]]" should compile }
    "support java.util.Calendar" in { "implicitly[ForRange[java.util.Calendar]]" should compile }
    "support java.sql.Date" in { "implicitly[ForRange[java.sql.Date]]" should compile }
    "support java.sql.Time" in { "implicitly[ForRange[java.sql.Time]]" should compile }
    "support java.time.Instant" in { "implicitly[ForRange[java.time.Instant]]" should compile }
    "support java.time.LocalDate" in { "implicitly[ForRange[java.time.LocalDate]]" should compile }
    "support java.time.LocalTime" in { "implicitly[ForRange[java.time.LocalTime]]" should compile }
    "support java.time.LocalDateTime" in { "implicitly[ForRange[java.time.LocalDateTime]]" should compile }
    "support java.time.OffsetDateTime" in { "implicitly[ForRange[java.time.OffsetDateTime]]" should compile }
    "support java.time.ZonedDateTime" in { "implicitly[ForRange[java.time.ZonedDateTime]]" should compile }
  }

}
