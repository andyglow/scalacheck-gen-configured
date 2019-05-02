package com.github.andyglow.scalacheck

import org.scalacheck.Gen

import scala.util.control.NonFatal


trait ForRange[T] {
  def apply(x: String): Either[String, Gen[T]]
}

object ForRange {

  // needed to allow compiler handle parse[Strings](...)
  implicit val stringFromString: ForRange[String] = new ForRange[String] {
    override def apply(x: String): Either[String, Gen[String]] = Left("range is not supported for strings")
  }

  implicit val intFromString: ForRange[Int] = create(_.toInt)

  implicit val longFromString: ForRange[Long] = create(_.toLong)

  implicit val doubleFromString: ForRange[Double] = create(_.toDouble)

  implicit val booleanFromString: ForRange[Boolean] = new ForRange[Boolean] {
    override def apply(x: String): Either[String, Gen[Boolean]] = Right(Gen.oneOf(true, false))
  }

  private def create[T: Gen.Choose](fn: String => T): ForRange[T] = new ForRange[T] {

    override def apply(x: String): Either[String, Gen[T]] = x.split("\\.\\.") match {
      case Array(min, max) =>
        try Right(Gen.choose(fn(min), fn(max)))
        catch {
          case NonFatal(err) => Left(err.getMessage)
        }
      case _ => Left(s"can't parse range: $x")
    }
  }

  def parse[T](dfn: String)(implicit fr: ForRange[T]): Either[String, Gen[T]] = fr(dfn)
}