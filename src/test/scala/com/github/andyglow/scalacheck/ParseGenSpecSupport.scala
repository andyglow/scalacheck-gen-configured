package com.github.andyglow.scalacheck

import java.time.{LocalDate, LocalDateTime, LocalTime}

import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalactic.source
import org.scalatest.Matchers.fail

import scala.reflect.runtime.universe._


object ParseGenSpecSupport {

  def doGen[T: ForConst: ForRange: ForOneOf: TypeTag](dfn: String): Either[String, T] = {
    ParseGen[T](dfn).right map { _.pureApply(Gen.Parameters.default, Seed.random) }
  }

  implicit class EitherOps[T](private val e: Either[String, T]) extends AnyVal {

    def value(implicit pos: source.Position): T = e match {
      case Right(value) => value
      case Left(error) => fail(error)
    }
  }

  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(_.toInstant(Tempo.systemZoneOffset).toEpochMilli)
  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
  implicit val localTimeOrdering: Ordering[LocalTime] = Ordering.by(_.toNanoOfDay)
}
