package com.github.andyglow.scalacheck.format.string

import com.github.andyglow.util.Scala212Compat._
import com.github.andyglow.scalacheck.{BoundsDefn, BoundsDefnPkg}

import scala.collection.mutable
import scala.util.Try

class BoundsParser[T: Numeric: Ordering](implicit pkg: BoundsDefnPkg[T], ap: BoundsParser.AcceptParse[T]) {
  import pkg._

  def parse(defn: Option[String]): Either[String, BoundsDefn[T]] =
    defn flatMap { x => Option(x).map(_.trim).filterNot(_.isEmpty) } map { parse(_) } getOrElse Right(Free)

  def parse(defn: String): Either[String, BoundsDefn[T]] = {
    val greater       = mutable.ListBuffer[GreaterThen]()
    val less          = mutable.ListBuffer[LessThen]()
    var exact: Exact  = null

    def ifExactIsNull(f: => Unit): Option[String] = if (exact != null) Some("exact is already set") else { f; None }

    val err = parse(defn.trim.toCharArray) { sd =>
      sd match {
        case sd: Exact        => ifExactIsNull { exact = sd }
        case sd: LessThen     => ifExactIsNull { less += sd }
        case sd: GreaterThen  => ifExactIsNull { greater += sd }
        case _                => Some("error")
      }
    }

    err toLeft {
      import Ordered._

      val minGreater = if (greater.nonEmpty) Some(greater.minBy(_.value).value) else None
      val maxLess    = if (less.nonEmpty)    Some(less.maxBy(_.value).value) else None

      (minGreater, maxLess) match {
        case (Some(g), None)              => GreaterThen(g)
        case (None, Some(l))              => LessThen(l)
        case (None, None)                 => if (exact != null) exact else Free
        case (Some(g), Some(l)) if g == l => Free
        case (Some(g), Some(l)) if g < l  => Inside(g, l)
        case (Some(g), Some(l)) if g > l  => Outside(l, g)
      }
    }
  }

  def parse(cs: Array[Char])(handler: BoundsDefn[T] => Option[String]): Option[String] = {
    sealed trait Token {
      def sb: StringBuilder
    }
    case class ExactToken(sb: StringBuilder = new StringBuilder) extends Token
    case class LessToken(sb: StringBuilder = new StringBuilder) extends Token
    case class GreaterToken(sb: StringBuilder = new StringBuilder) extends Token

    var t: Token = null

    def callback(): Option[String] = {
      if (t != null && t.sb.nonEmpty) {
        val res = handler {
          t match {
            case _: ExactToken    => Exact(ap.parse(t.sb))
            case _: GreaterToken  => GreaterThen(ap.parse(t.sb))
            case _: LessToken     => LessThen(ap.parse(t.sb))
          }
        }
        t = null
        res
      } else
        None
    }

    cs foreach { c =>
      c match {
        case '<' => if (t == null) t = LessToken() else return Some("error")
        case '>' => if (t == null) t = GreaterToken() else return Some("error")
        case c if ap.accept(c, Option(t) map { _.sb } filterNot { _.isEmpty }) =>
          if (t == null) t = ExactToken()
          t.sb.append(c)
        case c =>
          if (c.isWhitespace) {
            val res = callback()
            if (res.isDefined) return res
          } else return Some(s"error: $t: $c")
      }
    }

    callback()
  }
}

object BoundsParser {

  sealed trait AcceptParse[T] {
    def accept(c: Char, sb: Option[StringBuilder]): Boolean
    def parse(sb: StringBuilder): T
  }

  object AcceptParse {

    implicit val byteAP: AcceptParse[Byte] = new AcceptParse[Byte]() {
      override def accept(c: Char, sb: Option[StringBuilder]): Boolean = c.isDigit
      override def parse(sb: StringBuilder): Byte = sb.toString().toByte
    }

    implicit val shortAP: AcceptParse[Short] = new AcceptParse[Short]() {
      override def accept(c: Char, sb: Option[StringBuilder]): Boolean = (c, sb) match {
        case ('-', None)          => true
        case (c, _) if c.isDigit  => true
        case _                    => false
      }
      override def parse(sb: StringBuilder): Short = sb.toString().toShort
    }

    implicit val intAP: AcceptParse[Int] = new AcceptParse[Int]() {
      override def accept(c: Char, sb: Option[StringBuilder]): Boolean = (c, sb) match {
        case ('-', None)          => true
        case (c, _) if c.isDigit  => true
        case _                    => false
      }
      override def parse(sb: StringBuilder): Int = sb.toString().toInt
    }

    implicit val longAP: AcceptParse[Long] = new AcceptParse[Long]() {
      override def accept(c: Char, sb: Option[StringBuilder]): Boolean = (c, sb) match {
        case ('-', None)          => true
        case (c, _) if c.isDigit  => true
        case _                    => false
      }
      override def parse(sb: StringBuilder): Long = sb.toString().toLong
    }

    implicit val floatAP: AcceptParse[Float] = new AcceptParse[Float]() {
      override def accept(c: Char, sb: Option[StringBuilder]): Boolean = (c, sb) match {
        case ('-', None)          => true
        case (c, _) if c.isDigit  => true
        case ('.', None)  => true
        case ('.', Some(sb)) if !sb.contains('.')  => true
        case _                    => false
      }
      override def parse(sb: StringBuilder): Float = sb.toString().toFloat
    }

    implicit val doubleAP: AcceptParse[Double] = new AcceptParse[Double]() {
      override def accept(c: Char, sb: Option[StringBuilder]): Boolean = (c, sb) match {
        case ('-', None)          => true
        case (c, _) if c.isDigit  => true
        case ('.', None)  => true
        case ('.', Some(sb)) if !sb.contains('.')  => true
        case _                    => false
      }
      override def parse(sb: StringBuilder): Double = sb.toString().toDouble
    }
  }

  def apply[T: Numeric: Ordering: BoundsDefnPkg: AcceptParse]: BoundsParser[T] = new BoundsParser[T]()

}