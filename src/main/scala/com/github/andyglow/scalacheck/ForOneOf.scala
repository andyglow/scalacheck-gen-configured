package com.github.andyglow.scalacheck

import com.github.andyglow.util.Scala212Compat._
import org.scalacheck.Gen

import scala.util.control.NonFatal


trait ForOneOf[T] {

  def apply(x: String, xs: String*): Either[String, Gen[T]]
}

object ForOneOf {

  implicit val stringFromString: ForOneOf[String] = create(identity)

  implicit val booleanFromString: ForOneOf[Boolean] = new ForOneOf[Boolean] {
    override def apply(x: String, xs: String*): Either[String, Gen[Boolean]] = Right(Gen.oneOf(true, false))
  }

  implicit def oneOfFromConst[T: ForConst]: ForOneOf[T] = {
    val fc = implicitly[ForConst[T]]

    new ForOneOf[T] {

      override def apply(x: String, xs: String*): Either[String, Gen[T]] = {
        try {
          val gs = (x +: xs) flatMap { fc(_).toSeq }
          Right {
            Gen.choose(0, gs.length - 1) flatMap { gs(_) }
          }
        } catch {
          case NonFatal(err) => Left(err.getMessage)
        }
      }
    }
  }

  private def create[T](fn: String => T): ForOneOf[T] = new ForOneOf[T] {

    override def apply(x: String, xs: String*): Either[String, Gen[T]] = {
      try {
        val vals = (x +: xs) map { _.trim } filterNot { _.isEmpty } map fn
        Right(Gen.oneOf(vals))
      } catch {
        case NonFatal(err) => Left(err.getMessage)
      }
    }
  }

  def parse[T](x: String, xs: String*)(implicit fr: ForOneOf[T]): Either[String, Gen[T]] = fr(x, xs:_*)
}