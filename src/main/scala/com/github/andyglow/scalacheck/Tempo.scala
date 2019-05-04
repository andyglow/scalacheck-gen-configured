package com.github.andyglow.scalacheck

import java.time.format.DateTimeFormatter._
import java.time._
import java.util.Calendar

import scala.concurrent.duration._

private[andyglow] object Tempo {

  private lazy val dateTimeFmt =
    sys.props.get("scalacheck.gen.date-time.format") map ofPattern getOrElse ISO_LOCAL_DATE_TIME

  private lazy val zonedDateTimeFmt =
    (sys.props.get("scalacheck.gen.zoned-date-time.format") orElse
     sys.props.get("scalacheck.gen.date-time.format")) map ofPattern getOrElse ISO_ZONED_DATE_TIME

  private lazy val offsetDateTimeFmt =
    (sys.props.get("scalacheck.gen.offset-date-time.format") orElse
     sys.props.get("scalacheck.gen.date-time.format")) map ofPattern getOrElse ISO_OFFSET_DATE_TIME

  private lazy val dateFmt =
    sys.props.get("scalacheck.gen.date.format") map ofPattern getOrElse ISO_LOCAL_DATE

  private lazy val timeFmt =
    sys.props.get("scalacheck.gen.time.format") map ofPattern getOrElse ISO_LOCAL_TIME

  private[scalacheck] lazy val systemZoneOffset = OffsetDateTime.now.getOffset

  def localDateTimeOfMillis(x: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(x), systemZoneOffset)

  def localDateTimeMillis(x: LocalDateTime): Long =
    x.toInstant(systemZoneOffset).toEpochMilli

  private def parse[T](x: String)(fromDigits: Long => T, fromString: String => T): T =
    if (x.forall(_.isDigit)) fromDigits(x.toLong) else fromString(x)

  def parseLocalDateTime(x: String): LocalDateTime =
    parse(x)(
      localDateTimeOfMillis,
      LocalDateTime.parse(_, dateTimeFmt))

  def parseLocalDate(x: String): LocalDate =
    parse(x)(
      localDateTimeOfMillis(_).toLocalDate,
      LocalDate.parse(_, dateFmt))

  def parseLocalTime(x: String): LocalTime =
    parse(x)(
      localDateTimeOfMillis(_).toLocalTime,
      LocalTime.parse(_, timeFmt))

  def parseInstant(x: String): Instant =
    parse(x)(
      Instant.ofEpochMilli,
      Instant.parse)

  def parseCalendar(x: String): Calendar = {
    val dt = parseLocalDateTime(x)
    val c = Calendar.getInstance()
    c.clear()
    c.set(
      dt.getYear,
      dt.getMonthValue - 1,
      dt.getDayOfMonth,
      dt.getHour,
      dt.getMinute,
      dt.getSecond)
    c
  }

  def parseZonedDateTime(x: String): ZonedDateTime =
    parse(x)(
      localDateTimeOfMillis(_).atZone(systemZoneOffset),
      ZonedDateTime.parse(_, zonedDateTimeFmt))

  def parseOffsetDateTime(x: String): OffsetDateTime =
    parse(x)(
      localDateTimeOfMillis(_).atOffset(systemZoneOffset),
      OffsetDateTime.parse(_, offsetDateTimeFmt))

  def parseFiniteDuration(x: String): FiniteDuration = {
    val (nums, suffix) = x.span(_.isDigit)
    suffix.toLowerCase match {
      case "ns" =>  nums.toLong.nanoseconds
      case "ms" =>  nums.toLong.milliseconds
      case "s" =>  nums.toLong.seconds
      case "m" =>  nums.toLong.minutes
      case "h" =>  nums.toLong.hours
      case "d" =>  nums.toLong.days
    }
  }
}
