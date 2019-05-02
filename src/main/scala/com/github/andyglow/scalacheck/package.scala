package com.github.andyglow

import java.time.{Duration => _, _}
import java.util._

import com.github.andyglow.scalacheck.Tempo._
import org.scalacheck.Gen

import scala.concurrent.duration.Duration


package object scalacheck {

  //
  // DATETIME
  //

  implicit val dateChoose: Gen.Choose[Date] = Gen.Choose.xmap[Long, Date](
    millis => Date.from(Instant.ofEpochMilli(millis)),
    _.getTime)

  implicit val sqlDateChoose: Gen.Choose[java.sql.Date] = Gen.Choose.xmap[Long, java.sql.Date](
    millis => new java.sql.Date(millis),
    _.getTime)

  implicit val sqlTimeChoose: Gen.Choose[java.sql.Time] = Gen.Choose.xmap[Long, java.sql.Time](
    millis => new java.sql.Time(millis),
    _.getTime)

  implicit val calendarChoose: Gen.Choose[Calendar] = Gen.Choose.xmap[Long, Calendar](
    { millis =>
      val c = Calendar.getInstance()
      c.setTimeInMillis(millis)
      c
    },
    _.getTimeInMillis)

  implicit val instantChoose: Gen.Choose[Instant] = Gen.Choose.xmap[Long, Instant](Instant.ofEpochMilli, _.toEpochMilli)

  implicit val localDateTimeChoose: Gen.Choose[LocalDateTime] = Gen.Choose.xmap[Long, LocalDateTime](localDateTimeOfMillis, localDateTimeMillis)

  implicit val localDateChoose: Gen.Choose[LocalDate] = Gen.Choose.xmap[Long, LocalDate](LocalDate.ofEpochDay, _.toEpochDay)

  implicit val localTimeChoose: Gen.Choose[LocalTime] = Gen.Choose.xmap[Long, LocalTime](LocalTime.ofNanoOfDay, _.toNanoOfDay)

  implicit val zonedDateTimeChoose: Gen.Choose[ZonedDateTime] = Gen.Choose.xmap[Long, ZonedDateTime](
    millis => ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault),
    _.toInstant.toEpochMilli)

  implicit val offsetDateTimeChoose: Gen.Choose[OffsetDateTime] = Gen.Choose.xmap[Long, OffsetDateTime](
    millis => OffsetDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault),
    _.toInstant.toEpochMilli)

  //
  // DURATION
  //

  implicit val durationChoose: Gen.Choose[Duration] = Gen.Choose.xmap[Long, Duration](
    Duration.fromNanos,
    dur =>
      if (dur.isFinite) dur.toNanos else {
        if (dur == Duration.Inf) Long.MaxValue else Long.MinValue
      }
  )
}
