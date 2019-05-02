package com.github.andyglow.scalacheck

import org.scalacheck.Gen

import scala.util.control.NonFatal



trait ForOneOf[T] {
  def apply(x: String): Either[String, Gen[T]]
}

object ForOneOf {

  implicit val stringFromString: ForOneOf[String] = create(identity)

  implicit val intFromString: ForOneOf[Int] = create(_.toInt)

  implicit val longFromString: ForOneOf[Long] = create(_.toLong)

  implicit val doubleFromString: ForOneOf[Double] = create(_.toDouble)

  implicit val booleanFromString: ForOneOf[Boolean] = new ForOneOf[Boolean] {
    override def apply(x: String): Either[String, Gen[Boolean]] = Right(Gen.oneOf(true, false))
  }

  private def create[T](fn: String => T): ForOneOf[T] = new ForOneOf[T] {

    override def apply(x: String): Either[String, Gen[T]] = {
      val strings = x.split(',').map(_.trim).filterNot(_.isEmpty)
      try {
        val vals = strings map fn
        Right(Gen.oneOf(vals.toSeq))
      } catch {
        case NonFatal(err) => Left(err.getMessage)
      }
    }
  }

  def parse[T](dfn: String)(implicit fr: ForOneOf[T]): Either[String, Gen[T]] = fr(dfn)
}