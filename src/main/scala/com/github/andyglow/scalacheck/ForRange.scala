package com.github.andyglow.scalacheck

import com.github.andyglow.util.Result
import com.github.andyglow.util.Result._
import org.scalacheck.Gen

import scala.util.control.NonFatal

trait ForRange[T] {

  def apply(min: String, max: String): Result[Gen[T]]
}

object ForRange {

  // needed to allow compiler handle parse[Strings](...)
  implicit val stringFromString: ForRange[String] = new ForRange[String] {
    override def apply(min: String, max: String): Result[Gen[String]] = Error("range is not supported for strings")
  }

  implicit val booleanFromString: ForRange[Boolean] = new ForRange[Boolean] {
    override def apply(min: String, max: String): Result[Gen[Boolean]] = Error("range is not supported for booleans")
  }

  implicit def rangeForConst[T: ForConst: Gen.Choose]: ForRange[T] = {
    val fc = implicitly[ForConst[T]]

    create(fc.crack)
  }

  private def create[T: Gen.Choose](fn: String => T): ForRange[T] = new ForRange[T] {

    override def apply(min: String, max: String): Result[Gen[T]] = {
      try Ok(Gen.choose(fn(min.trim), fn(max.trim))) catch {
        case NonFatal(err) => Error(err.getMessage)
      }
    }
  }

  def parse[T](min: String, max: String)(implicit fr: ForRange[T]): Result[Gen[T]] = fr(min, max)
}