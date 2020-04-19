package com.github.andyglow.scalacheck

import java.time.{LocalDate, LocalDateTime, LocalTime}

import com.github.andyglow.util.Result
import com.github.andyglow.util.Result._
import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalactic.source

import scala.reflect.runtime.universe._
import org.scalatest.matchers.should.Matchers._


object ParseGenSpecSupport {

  def doGen[T: ForConst: ForRange: ForOneOf: TypeTag](dfn: String): Result[T] = {
    MakeGen[T, String](dfn) map { _.pureApply(Gen.Parameters.default, Seed.random) }
  }

  implicit class ResultOps[T](private val e: Result[T]) extends AnyVal {

    def value(implicit pos: source.Position): T = e match {
      case Ok(value) => value
      case Error(error) => fail(error)
    }
  }

  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(_.toInstant(Tempo.systemZoneOffset).toEpochMilli)
  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
  implicit val localTimeOrdering: Ordering[LocalTime] = Ordering.by(_.toNanoOfDay)
}
