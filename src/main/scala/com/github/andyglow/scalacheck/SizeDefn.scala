package com.github.andyglow.scalacheck

import com.github.andyglow.util.Scala212Compat._

import scala.util.Try

sealed trait SizeDefn

object SizeDefn {

  case class Strict(value: Int) extends SizeDefn

  case class GreaterThen(value: Int) extends SizeDefn

  case class LessThen(value: Int) extends SizeDefn

  case object Free extends SizeDefn

  def parse(defn: Option[String]): Either[String, SizeDefn] =
    defn flatMap { x => Option(x).map(_.trim).filterNot(_.isEmpty) } map { parse(_) } getOrElse Right(Free)

  def parse(defn: String): Either[String, SizeDefn] = {
    def parseNum(str: String): Either[String, Int] = for {
      num     <- Try(Right(str.trim.toInt)).recover { case err => Left(s"can't parse '$str': ${err.getMessage}. number expected") }.get
      posNum  <- if (num >= 0) Right(num) else Left(s"size can not be defined with negative number $num")
    } yield posNum

    defn.trim.toLowerCase match {
      case str if str.startsWith(">")   => parseNum(str.drop(1)) map GreaterThen.apply
      case str if str.startsWith("<")   => parseNum(str.drop(1)) filterOrElse(_ > 0, "size can not be defined as 0") map LessThen.apply
      case str if str.forall(_.isDigit) => Right(Strict(str.toInt))
      case str                          => Left(s"unknown size: $str")
    }
  }
}