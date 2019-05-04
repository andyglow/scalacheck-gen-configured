package com.github.andyglow.scalacheck

import org.scalacheck.Gen

import scala.util.control.NonFatal

trait ForRange[T] {

  def apply(min: String, max: String): Either[String, Gen[T]]
}

object ForRange {

  // needed to allow compiler handle parse[Strings](...)
  implicit val stringFromString: ForRange[String] = new ForRange[String] {
    override def apply(min: String, max: String): Either[String, Gen[String]] = Left("range is not supported for strings")
  }

  implicit val booleanFromString: ForRange[Boolean] = new ForRange[Boolean] {
    override def apply(min: String, max: String): Either[String, Gen[Boolean]] = Left("range is not supported for booleans")
  }

  implicit def rangeForConst[T: ForConst: Gen.Choose]: ForRange[T] = {
    val fc = implicitly[ForConst[T]]

    create(fc.crack)
  }

  private def create[T: Gen.Choose](fn: String => T): ForRange[T] = new ForRange[T] {

    override def apply(min: String, max: String): Either[String, Gen[T]] = {
      try Right(Gen.choose(fn(min.trim), fn(max.trim))) catch {
        case NonFatal(err) => Left(err.getMessage)
      }
    }
  }

  def parse[T](min: String, max: String)(implicit fr: ForRange[T]): Either[String, Gen[T]] = fr(min, max)
}