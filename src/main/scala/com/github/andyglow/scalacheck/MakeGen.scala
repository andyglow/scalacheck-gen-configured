package com.github.andyglow.scalacheck

import com.github.andyglow.util.Result
import org.scalacheck.Gen

import scala.reflect.runtime.universe._


object MakeGen {

  def apply[T: ForConst: ForRange: ForOneOf: TypeTag, D: DefnFormat](defn: D): Result[Gen[T]] = {
    for {
      defn <- DefnFormat[D] make defn
      gen  <- defn.gen[T]
    } yield gen
  }
}
