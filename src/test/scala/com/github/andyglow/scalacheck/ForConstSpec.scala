package com.github.andyglow.scalacheck

import java.time._

import org.scalatest._
import org.scalatest.Matchers._
import com.github.andyglow.util.Scala212Compat._
import SpecSupport._


class ForConstSpec extends WordSpec {

  "ForConst" should {

    "support" when {
      "Byte" in { "implicitly[ForConst[Byte]]" should compile }
      "Short" in { "implicitly[ForConst[Short]]" should compile }
      "Int" in { "implicitly[ForConst[Int]]" should compile }
      "Long" in { "implicitly[ForConst[Long]]" should compile }
      "Float" in { "implicitly[ForConst[Float]]" should compile }
      "Double" in { "implicitly[ForConst[Double]]" should compile }
      "Char" in { "implicitly[ForConst[Char]]" should compile }
      "String" in { "implicitly[ForConst[String]]" should compile }
      "Boolean" in { "implicitly[ForConst[Boolean]]" should compile }
      "java.util.Date" in { "implicitly[ForConst[java.util.Date]]" should compile }
      "java.util.Calendar" in { "implicitly[ForConst[java.util.Calendar]]" should compile }
      "java.sql.Date" in { "implicitly[ForConst[java.sql.Date]]" should compile }
      "java.sql.Time" in { "implicitly[ForConst[java.sql.Time]]" should compile }
      "java.time.Instant" in { "implicitly[ForConst[java.time.Instant]]" should compile }
      "java.time.LocalDate" in { "implicitly[ForConst[java.time.LocalDate]]" should compile }
      "java.time.LocalTime" in { "implicitly[ForConst[java.time.LocalTime]]" should compile }
      "java.time.LocalDateTime" in { "implicitly[ForConst[java.time.LocalDateTime]]" should compile }
      "java.time.OffsetDateTime" in { "implicitly[ForConst[java.time.OffsetDateTime]]" should compile }
      "java.time.ZonedDateTime" in { "implicitly[ForConst[java.time.ZonedDateTime]]" should compile }
    }

    "parse" when {
      "Byte" in { ForConst.parse[Byte]("12").value shouldBe 12 }
      "Short" in { ForConst.parse[Short]("12").value shouldBe 12 }
      "Int" in { ForConst.parse[Int]("12").value shouldBe 12 }
      "Long" in { ForConst.parse[Long]("12").value shouldBe 12 }
      "Float" in { ForConst.parse[Float]("12").value shouldBe 12 }
      "Double" in { ForConst.parse[Double]("12").value shouldBe 12 }
      "Char" in { List("z", "zoo") foreach { str => ForConst.parse[Char](str).value shouldBe 'z' } }
      "String" in { ForConst.parse[String]("foo").value shouldBe "foo" }
      "Boolean. False" in { ForConst.parse[Boolean]("false").value shouldBe false }
      "Boolean. True" in { ForConst.parse[Boolean]("true").value shouldBe true }
      "java.util.Date" in { ForConst.parse[java.util.Date]("2018-03-06T11:34").value shouldBe new java.util.Date(1520364840000l) }
      "java.util.Calendar" in { ForConst.parse[java.util.Calendar]("2018-03-06T11:34").value shouldBe java.util.Calendar.getInstance().withEpochMillis(1520364840000l) }
      "java.sql.Date" in { ForConst.parse[java.sql.Date]("2018-03-06").value shouldBe java.sql.Date.valueOf("2018-03-06") }
      "java.sql.Time" in { ForConst.parse[java.sql.Time]("03:06").value shouldBe java.sql.Time.valueOf("03:06:00") }
      "java.time.Instant" in { ForConst.parse[java.time.Instant]("2018-03-06T11:34:00Z").value shouldBe Instant.parse("2018-03-06T11:34:00Z") }
      "java.time.LocalDateTime" in { ForConst.parse[java.time.LocalDateTime]("2018-03-06T11:34:00").value shouldBe LocalDateTime.parse("2018-03-06T11:34:00") }
      "java.time.LocalDate" in { ForConst.parse[java.time.LocalDate]("2018-03-06").value shouldBe LocalDate.parse("2018-03-06") }
      "java.time.LocalTime" in { ForConst.parse[java.time.LocalTime]("11:34").value shouldBe LocalTime.parse("11:34:00") }
      "java.time.OffsetDateTime" in { ForConst.parse[java.time.OffsetDateTime]("2018-03-06T11:34:00+02:00").value shouldBe OffsetDateTime.parse("2018-03-06T11:34:00+02:00") }
      "java.time.ZonedDateTime" in { ForConst.parse[java.time.ZonedDateTime]("2018-03-06T11:34:00+01:00").value shouldBe ZonedDateTime.parse("2018-03-06T11:34:00+01:00") }
    }

    "fail parsing" when {
      "Byte if not number" in { ForConst.parse[Byte]("z").generate shouldBe 'left }
      "Byte if empty" in { ForConst.parse[Byte]("").generate shouldBe 'left }
      "Short if not number" in { ForConst.parse[Short]("z").generate shouldBe 'left }
      "Short if empty" in { ForConst.parse[Short]("").generate shouldBe 'left }
      "Int if not number" in { ForConst.parse[Int]("z").generate shouldBe 'left }
      "Int if empty" in { ForConst.parse[Int]("").generate shouldBe 'left }
      "Long if not number" in { ForConst.parse[Long]("z").generate shouldBe 'left }
      "Long if empty" in { ForConst.parse[Long]("").generate shouldBe 'left }
      "Float if not number" in { ForConst.parse[Float]("z").generate shouldBe 'left }
      "Float if empty" in { ForConst.parse[Float]("").generate shouldBe 'left }
      "Double if not number" in { ForConst.parse[Double]("z").generate shouldBe 'left }
      "Double if empty" in { ForConst.parse[Float]("").generate shouldBe 'left }
      "Char if empty" in { ForConst.parse[Char]("").generate shouldBe 'left }
      "Boolean if not true or false" in { ForConst.parse[Boolean]("zzz").generate shouldBe 'left }
      "Boolean if empty" in { ForConst.parse[Boolean]("").generate shouldBe 'left }
    }
  }
}