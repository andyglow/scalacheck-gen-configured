package com.github.andyglow.scalacheck

import com.github.andyglow.util.Scala212Compat._
import org.scalacheck.Gen

import scala.reflect.runtime.universe._


object ParseGen {

  def apply[T: ForConst: ForRange: ForOneOf: TypeTag](value: String): Either[String, Gen[T]] = {
    for {
      defn <- GenDefn.parse(value)
      gen  <- defn.gen[T]
    } yield gen
  }
}
