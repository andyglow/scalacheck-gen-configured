package com.github.andyglow.scalacheck.format.string

import com.github.andyglow.scalacheck.{DefnFormat, GenDefn, PrefixedString}
import com.github.andyglow.util.Result
import com.github.andyglow.util.Result._


object StringDefnFormat extends DefnFormat[String] {
  import GenDefn._

  override def make(defn: String): Result[GenDefn] = {
    defn match {
      case "numChar"            => Ok(numChar)
      case "alphaUpperChar"     => Ok(alphaUpperChar)
      case "alphaLowerChar"     => Ok(alphaLowerChar)
      case "alphaChar"          => Ok(alphaChar)
      case "alphaNumChar"       => Ok(alphaNumChar)
      case "asciiChar"          => Ok(asciiChar)
      case "asciiPrintableChar" => Ok(asciiPrintableChar)
      case "posNum"             => Ok(posNum)
      case "negNum"             => Ok(negNum)

      case PrefixedString(prefix, defn) =>
        (prefix, defn) match {
          case ("identifier", _)         => ExprParser[Int].parse(defn) map identifier.apply
          case ("numStr", _)             => ExprParser[Int].parse(defn) map numStr.apply
          case ("alphaUpperStr", _)      => ExprParser[Int].parse(defn) map alphaUpperStr.apply
          case ("alphaLowerStr", _)      => ExprParser[Int].parse(defn) map alphaLowerStr.apply
          case ("alphaStr", _)           => ExprParser[Int].parse(defn) map alphaStr.apply
          case ("alphaNumStr", _)        => ExprParser[Int].parse(defn) map alphaNumStr.apply
          case ("asciiStr", _)           => ExprParser[Int].parse(defn) map asciiStr.apply
          case ("asciiPrintableStr", _)  => ExprParser[Int].parse(defn) map asciiPrintableStr.apply

          case ("const", Some(defn))                => Ok(const(defn))
          case ("range", Some(RangeStmt(min, max))) => Ok(range(min, max))
          case ("oneof", Some(OneOfStmt(items)))    => Ok(oneof(items.head, items.tail:_*))
          case _                                    => Error(s"can't parse $defn")
        }

      case _ => Error(s"can't parse $defn")
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
