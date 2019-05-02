package com.github.andyglow.scalacheck

import java.util.{Calendar, UUID}

import org.scalacheck.Gen

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.reflect.runtime.universe._


object ParseGen {

  def apply[T: ForConst: ForRange: ForOneOf: TypeTag](value: String): Either[String, Gen[T]] = {
    val tt = typeOf[T]

    def unsupported: Either[String, Gen[T]] = Left(s"$value is not supported for ${tt.typeSymbol.fullName}")

    value match {
      case "numChar"               => if (tt =:= typeOf[Char]) Right(Gen.numChar.asInstanceOf[Gen[T]]) else unsupported
      case "alphaUpperChar"        => if (tt =:= typeOf[Char]) Right(Gen.alphaUpperChar.asInstanceOf[Gen[T]]) else unsupported
      case "alphaLowerChar"        => if (tt =:= typeOf[Char]) Right(Gen.alphaLowerChar.asInstanceOf[Gen[T]]) else unsupported
      case "alphaChar"             => if (tt =:= typeOf[Char]) Right(Gen.alphaChar.asInstanceOf[Gen[T]]) else unsupported
      case "alphaNumChar"          => if (tt =:= typeOf[Char]) Right(Gen.alphaNumChar.asInstanceOf[Gen[T]]) else unsupported
      case "asciiChar"             => if (tt =:= typeOf[Char]) Right(Gen.asciiChar.asInstanceOf[Gen[T]]) else unsupported
      case "asciiPrintableChar"    => if (tt =:= typeOf[Char]) Right(Gen.asciiPrintableChar.asInstanceOf[Gen[T]]) else unsupported

      case "identifier"         => if (tt =:= typeOf[String]) Right(Gen.identifier.asInstanceOf[Gen[T]]) else unsupported
      case "numStr"             => if (tt =:= typeOf[String]) Right(Gen.numStr.asInstanceOf[Gen[T]]) else unsupported
      case "alphaUpperStr"      => if (tt =:= typeOf[String]) Right(Gen.alphaUpperStr.asInstanceOf[Gen[T]]) else unsupported
      case "alphaLowerStr"      => if (tt =:= typeOf[String]) Right(Gen.alphaLowerStr.asInstanceOf[Gen[T]]) else unsupported
      case "alphaStr"           => if (tt =:= typeOf[String]) Right(Gen.alphaStr.asInstanceOf[Gen[T]]) else unsupported
      case "alphaNumStr"        => if (tt =:= typeOf[String]) Right(Gen.alphaNumStr.asInstanceOf[Gen[T]]) else unsupported
      case "asciiStr"           => if (tt =:= typeOf[String]) Right(Gen.asciiStr.asInstanceOf[Gen[T]]) else unsupported
      case "asciiPrintableStr"  => if (tt =:= typeOf[String]) Right(Gen.asciiPrintableStr.asInstanceOf[Gen[T]]) else unsupported

      case "posNum" if tt =:= typeOf[Byte]   => Right(Gen.posNum[Byte].asInstanceOf[Gen[T]])
      case "posNum" if tt =:= typeOf[Short]  => Right(Gen.posNum[Short].asInstanceOf[Gen[T]])
      case "posNum" if tt =:= typeOf[Int]    => Right(Gen.posNum[Int].asInstanceOf[Gen[T]])
      case "posNum" if tt =:= typeOf[Long]   => Right(Gen.posNum[Long].asInstanceOf[Gen[T]])
      case "posNum" if tt =:= typeOf[Float]  => Right(Gen.posNum[Float].asInstanceOf[Gen[T]])
      case "posNum" if tt =:= typeOf[Double] => Right(Gen.posNum[Double].asInstanceOf[Gen[T]])
      case "posNum" => unsupported

      case "negNum" if tt =:= typeOf[Byte]   => Right(Gen.negNum[Byte].asInstanceOf[Gen[T]])
      case "negNum" if tt =:= typeOf[Short]  => Right(Gen.negNum[Short].asInstanceOf[Gen[T]])
      case "negNum" if tt =:= typeOf[Int]    => Right(Gen.negNum[Int].asInstanceOf[Gen[T]])
      case "negNum" if tt =:= typeOf[Long]   => Right(Gen.negNum[Long].asInstanceOf[Gen[T]])
      case "negNum" if tt =:= typeOf[Float]  => Right(Gen.negNum[Float].asInstanceOf[Gen[T]])
      case "negNum" if tt =:= typeOf[Double] => Right(Gen.negNum[Double].asInstanceOf[Gen[T]])
      case "negNum"  => unsupported

        // TODO: allow range
        // TODO: java.time
//      case "calendar" if tt =:= typeOf[Calendar]              => Right(Gen.calendar.asInstanceOf[Gen[T]])
//      case "duration" if tt =:= typeOf[Duration]              => Right(Gen.duration.asInstanceOf[Gen[T]])
//      case "finite-duration" if tt =:= typeOf[FiniteDuration] => Right(Gen.finiteDuration.asInstanceOf[Gen[T]])

      case StringTuple(tpe, dfn) =>
        tpe match {
          case "const" => ForConst.parse[T](dfn)
          case "range" => ForRange.parse[T](dfn)
          case "oneof" => ForOneOf.parse[T](dfn)
          case _       => Left(s"can't parse: '$value'")
        }
    }
  }

  object StringTuple {

    def unapply(x: String): Option[(String, String)] = x.split(':') match {
      case Array(l, r) => Some((l.trim, r.trim))
      case _ => None
    }
  }
}
