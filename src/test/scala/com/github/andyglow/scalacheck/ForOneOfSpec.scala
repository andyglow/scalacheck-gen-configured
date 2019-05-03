package com.github.andyglow.scalacheck

import org.scalatest.Matchers._
import org.scalatest._

class ForOneOfSpec extends WordSpec {

  "ForOneOf" should {
    "support Byte" in { "implicitly[ForOneOf[Byte]]" should compile }
    "support Short" in { "implicitly[ForOneOf[Short]]" should compile }
    "support Int" in { "implicitly[ForOneOf[Int]]" should compile }
    "support Long" in { "implicitly[ForOneOf[Long]]" should compile }
    "support Float" in { "implicitly[ForOneOf[Float]]" should compile }
    "support Double" in { "implicitly[ForOneOf[Double]]" should compile }
    "support Char" in { "implicitly[ForOneOf[Char]]" should compile }
    "support String" in { "implicitly[ForOneOf[String]]" should compile }
    "support Boolean" in { "implicitly[ForOneOf[Boolean]]" should compile }
    "support java.util.Date" in { "implicitly[ForOneOf[java.util.Date]]" should compile }
    "support java.util.Calendar" in { "implicitly[ForOneOf[java.util.Calendar]]" should compile }
    "support java.sql.Date" in { "implicitly[ForOneOf[java.sql.Date]]" should compile }
    "support java.sql.Time" in { "implicitly[ForOneOf[java.sql.Time]]" should compile }
    "support java.time.Instant" in { "implicitly[ForOneOf[java.time.Instant]]" should compile }
    "support java.time.LocalDate" in { "implicitly[ForOneOf[java.time.LocalDate]]" should compile }
    "support java.time.LocalTime" in { "implicitly[ForOneOf[java.time.LocalTime]]" should compile }
    "support java.time.LocalDateTime" in { "implicitly[ForOneOf[java.time.LocalDateTime]]" should compile }
    "support java.time.OffsetDateTime" in { "implicitly[ForOneOf[java.time.OffsetDateTime]]" should compile }
    "support java.time.ZonedDateTime" in { "implicitly[ForOneOf[java.time.ZonedDateTime]]" should compile }
  }

}
