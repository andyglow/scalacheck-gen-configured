package com.github.andyglow.scalacheck

import org.scalacheck.Gen

import scala.util.control.NonFatal


trait ForConst[T] {

  def apply(x: String): Either[String, Gen[T]]
}

object ForConst {


  implicit val stringFromString: ForConst[String] = create(_.trim)

  implicit val intFromString: ForConst[Int] = create(_.toInt)

  implicit val longFromString: ForConst[Long] = create(_.toLong)

  implicit val doubleFromString: ForConst[Double] = create(_.toDouble)

  implicit val booleanFromString: ForConst[Boolean] = create(_.toBoolean)

  private def create[T](fn: String => T): ForConst[T] = new ForConst[T] {

    override def apply(x: String): Either[String, Gen[T]] =
      try Right(Gen.const(fn(x)))
      catch {
        case NonFatal(err) => Left(err.getMessage)
      }
  }

  def parse[T](dfn: String)(implicit fc: ForConst[T]): Either[String, Gen[T]] = fc(dfn)
}