package com.github.andyglow.scalacheck

import java.time.Instant
import java.util.Calendar

import com.github.andyglow.util.Result
import com.github.andyglow.util.Result._
import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalatest.matchers.should.Matchers._


object SpecSupport {

  private val seed = Seed.random

  implicit class ResultOps[T](private val e: Result[Gen[T]]) extends AnyVal {

    def generate: Result[T] = e.map(_.pureApply(Gen.Parameters.default, seed))

    def value: T = generate match {
      case Ok(v)      => v
      case Error(err) => fail(err)
    }
  }

  implicit class CalendarOps(private val c: Calendar) extends AnyVal {

    def withEpochMillis(x: Long): Calendar = {
      val copy = c.clone().asInstanceOf[Calendar]
      copy.setTimeInMillis(x)
      copy
    }

    def withInstant(x: Instant): Calendar = {
      val copy = c.clone().asInstanceOf[Calendar]
      copy.setTimeInMillis(x.toEpochMilli)
      copy
    }
  }
}
