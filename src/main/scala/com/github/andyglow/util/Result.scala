package com.github.andyglow.util

/** Substitution for Either.
  * The reason we do that is difference in api of 2.11 and 2.12+ versions.
  *
  * @tparam T
  */
trait Result[+T] {
  import Result._

  def isOk: Boolean
  def isRight: Boolean = isOk

  def isError: Boolean = !isOk
  def isLeft: Boolean = isError

  def get: T

  def map[R](f: T => R): Result[R] = if (isOk) Ok(f(get)) else this.asInstanceOf[Result[R]]

  def flatMap[R](f: T => Result[R]): Result[R] = if (isOk) f(get) else this.asInstanceOf[Result[R]]

  def filterOrElse[TT >: T](p: TT => Boolean, zero: => TT): Result[TT] = this match {
    case Ok(b) if !p(b) => Ok(zero)
    case _              => this
  }

  def toEither: Either[String, T]

  def toOption: Option[T]

  def toSeq: Seq[T]
}

object Result {

  final case class Ok[+T](value: T) extends Result[T] {
    def get: T = value
    def isOk: Boolean = true
    def toEither: Either[String, T] = Right(value)
    def toOption: Option[T] = Some(value)
    def toSeq: Seq[T] = Seq(value)
  }

  final case class Error(message: String) extends Result[Nothing] {
    def get: Nothing = throw new IllegalStateException
    def isOk: Boolean = false
    def toEither: Either[String, Nothing] = Left(message)
    def toOption: Option[Nothing] = None
    def toSeq: Seq[Nothing] = Seq.empty
  }

  implicit class StringOptionOps(private val x: Option[String]) extends AnyVal {

    def toError[T](ok: => T): Result[T] = x match {
      case Some(v) => Error(v)
      case None    => Ok(ok)
    }
  }
}