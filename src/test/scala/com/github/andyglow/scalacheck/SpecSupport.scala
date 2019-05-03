package com.github.andyglow.scalacheck

import java.util.Calendar

import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalatest.Matchers._
import com.github.andyglow.util.Scala212Compat._


object SpecSupport {

  private val seed = Seed.random

  implicit class EitherErrorOps[T](private val e: Either[String, Gen[T]]) extends AnyVal {

    def generate: Either[String, T] = e.map(_.pureApply(Gen.Parameters.default, seed))

    def value: T = generate match {
      case Right(v)   => v
      case Left(err)  => fail(err)
    }
  }

  implicit class CalendarOps(private val c: Calendar) extends AnyVal {

    def withEpochMillis(x: Long): Calendar = {
      val copy = c.clone().asInstanceOf[Calendar]
      copy.setTimeInMillis(x)
      copy
    }
  }
}
