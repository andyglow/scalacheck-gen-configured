package com.github.andyglow.scalacheck

import com.github.andyglow.util.Result
import com.github.andyglow.util.Result._
import org.scalacheck.Gen

import scala.util.control.NonFatal


trait ForOneOf[T] {

  def apply(x: String, xs: String*): Result[Gen[T]]
}

object ForOneOf {

  implicit val stringFromString: ForOneOf[String] = create(identity)

  implicit val booleanFromString: ForOneOf[Boolean] = new ForOneOf[Boolean] {
    override def apply(x: String, xs: String*): Result[Gen[Boolean]] = Ok(Gen.oneOf(true, false))
  }

  implicit def oneOfFromConst[T: ForConst]: ForOneOf[T] = {
    val fc = implicitly[ForConst[T]]

    new ForOneOf[T] {

      override def apply(x: String, xs: String*): Result[Gen[T]] = {
        try {
          val gs = (x +: xs) flatMap { fc(_).toSeq }
          Ok {
            Gen.choose(0, gs.length - 1) flatMap { gs(_) }
          }
        } catch {
          case NonFatal(err) => Error(err.getMessage)
        }
      }
    }
  }

  private def create[T](fn: String => T): ForOneOf[T] = new ForOneOf[T] {

    override def apply(x: String, xs: String*): Result[Gen[T]] = {
      try {
        val vals = (x +: xs) map { _.trim } filterNot { _.isEmpty } map fn
        Ok(Gen.oneOf(vals))
      } catch {
        case NonFatal(err) => Error(err.getMessage)
      }
    }
  }

  def parse[T](x: String, xs: String*)(implicit fr: ForOneOf[T]): Result[Gen[T]] = fr(x, xs:_*)
}