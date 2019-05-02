package com.github.andyglow.scalacheck

import com.github.andyglow.util.Scala212Compat._
import org.scalacheck.Gen

import scala.util.control.NonFatal


trait ForOneOf[T] {

  def apply(x: String): Either[String, Gen[T]]
}

object ForOneOf {

  implicit val stringFromString: ForOneOf[String] = create(identity)

  implicit val booleanFromString: ForOneOf[Boolean] = new ForOneOf[Boolean] {
    override def apply(x: String): Either[String, Gen[Boolean]] = Right(Gen.oneOf(true, false))
  }

  private def split(x: String): Array[String] = x.split(',').map(_.trim).filterNot(_.isEmpty)

  implicit def oneOfFromConst[T: ForConst]: ForOneOf[T] = {
    val fc = implicitly[ForConst[T]]

    new ForOneOf[T] {
      override def apply(x: String): Either[String, Gen[T]] = {
        try {
          val gs = split(x) flatMap { x => fc(x).toSeq }
          Right {
            Gen.choose(0, gs.length - 1) flatMap { idx => gs(idx) }
          }
        } catch {
          case NonFatal(err) => Left(err.getMessage)
        }
      }
    }
  }

  private def create[T](fn: String => T): ForOneOf[T] = new ForOneOf[T] {

    override def apply(x: String): Either[String, Gen[T]] = {
      try {
        val vals = split(x) map fn
        Right(Gen.oneOf(vals.toSeq))
      } catch {
        case NonFatal(err) => Left(err.getMessage)
      }
    }
  }

  def parse[T](dfn: String)(implicit fr: ForOneOf[T]): Either[String, Gen[T]] = fr(dfn)
}