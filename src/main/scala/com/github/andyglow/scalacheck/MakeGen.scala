package com.github.andyglow.scalacheck

import com.github.andyglow.util.Scala212Compat._
import org.scalacheck.Gen

import scala.reflect.runtime.universe._


object MakeGen {

  def apply[T: ForConst: ForRange: ForOneOf: TypeTag, D: DefnFormat](defn: D): Either[String, Gen[T]] = {
    for {
      defn <- DefnFormat[D] make defn
      gen  <- defn.gen[T]
    } yield gen
  }
}
