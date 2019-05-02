package com.github.andyglow.util


object Scala212Compat {

  implicit class EitherOps[+L, +R](private val e: Either[L, R]) extends AnyVal {

    def map[RR](f: R => RR): Either[L, RR] = e.right map f

    def flatMap[LL >: L, RR](f: R => Either[LL, RR]): Either[LL, RR] = e.right flatMap f

    def toOption: Option[R] = e.right.toOption

    def toSeq: Seq[R] = e.right.toSeq

    def filterOrElse[LL >: L](p: R => Boolean, zero: => LL): Either[LL, R] = e match {
      case Right(b) if !p(b) => Left(zero)
      case _                 => e
    }
  }
}
