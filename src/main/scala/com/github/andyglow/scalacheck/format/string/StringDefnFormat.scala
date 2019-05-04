package com.github.andyglow.scalacheck.format.string

import com.github.andyglow.scalacheck.{GenDefn, PrefixedString, BoundsDefn, DefnFormat}
import com.github.andyglow.util.Scala212Compat._

import scala.util.Try


object StringDefnFormat extends DefnFormat[String] {
  import GenDefn._

  override def make(defn: String): Either[String, GenDefn] = {
    defn match {
      case "numChar"            => Right(numChar)
      case "alphaUpperChar"     => Right(alphaUpperChar)
      case "alphaLowerChar"     => Right(alphaLowerChar)
      case "alphaChar"          => Right(alphaChar)
      case "alphaNumChar"       => Right(alphaNumChar)
      case "asciiChar"          => Right(asciiChar)
      case "asciiPrintableChar" => Right(asciiPrintableChar)
      case "posNum"             => Right(posNum)
      case "negNum"             => Right(negNum)

      case PrefixedString(prefix, defn) =>
        (prefix, defn) match {
          case ("identifier", _)         => BoundsParser[Int].parse(defn) map identifier.apply
          case ("numStr", _)             => BoundsParser[Int].parse(defn) map numStr.apply
          case ("alphaUpperStr", _)      => BoundsParser[Int].parse(defn) map alphaUpperStr.apply
          case ("alphaLowerStr", _)      => BoundsParser[Int].parse(defn) map alphaLowerStr.apply
          case ("alphaStr", _)           => BoundsParser[Int].parse(defn) map alphaStr.apply
          case ("alphaNumStr", _)        => BoundsParser[Int].parse(defn) map alphaNumStr.apply
          case ("asciiStr", _)           => BoundsParser[Int].parse(defn) map asciiStr.apply
          case ("asciiPrintableStr", _)  => BoundsParser[Int].parse(defn) map asciiPrintableStr.apply

          case ("const", Some(defn))                => Right(const(defn))
          case ("range", Some(RangeStmt(min, max))) => Right(range(min, max))
          case ("oneof", Some(OneOfStmt(items)))    => Right(oneof(items.head, items.tail:_*))
          case _                                    => Left(s"can't parse $defn")
        }

      case _ => Left(s"can't parse $defn")
    }
  }

  object RangeStmt {

    def unapply(x: String): Option[(String, String)] = {
      x.split("\\.\\.") match {
        case Array(min, max)  => Some((min, max))
        case _                => None
      }
    }
  }

  object OneOfStmt {

    private def split(x: String): List[String] = x.split(',').map(_.trim).filterNot(_.isEmpty).toList

    def unapply(x: String): Option[::[String]] = {
      val l = split(x)
      if (l.nonEmpty) Some(new ::(l.head, l.tail)) else None
    }
  }
}
