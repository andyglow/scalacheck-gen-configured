package com.github.andyglow.scalacheck

import com.github.andyglow.util.Scala212Compat._
import org.scalacheck.Gen

import scala.reflect.runtime.universe._


sealed trait GenDefn extends Product {

  def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Either[String, Gen[T]]
}

object GenDefn {

  // char
  sealed abstract class CharGenDefn(g: Gen[Char]) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Either[String, Gen[T]] = {
      val t = typeOf[T]
      if (typeOf[T] =:= typeOf[Char]) Right(g.asInstanceOf[Gen[T]])
      else Left(s"unsupported type ${t.typeSymbol.name.decodedName.toString}; Char expected")
    }
  }

  case object numChar extends CharGenDefn(Gen.numChar)
  case object alphaUpperChar extends CharGenDefn(Gen.alphaUpperChar)
  case object alphaLowerChar extends CharGenDefn(Gen.alphaLowerChar)
  case object alphaChar extends CharGenDefn(Gen.alphaChar)
  case object alphaNumChar extends CharGenDefn(Gen.alphaNumChar)
  case object asciiChar extends CharGenDefn(Gen.asciiChar)
  case object asciiPrintableChar extends CharGenDefn(Gen.asciiPrintableChar)

  // string
  sealed abstract class StringGenDefn(firstCharGen: Gen[Char], charGen: Gen[Char], size: SizeDefn) extends GenDefn {
    import SizeDefn._

    private val g = size match {
      case Free => for {
        c <- firstCharGen
        cs <- Gen.listOf(charGen)
      } yield (c::cs).mkString

      case Strict(0) | LessThen(1) => Gen.const("")

      case Strict(1) | LessThen(2) => firstCharGen map { _.toString }

      case Strict(len) => for {
        c  <- firstCharGen
        cs <- Gen.listOfN(len - 1, charGen)
      } yield (c :: cs).mkString

      case GreaterThen(len) => for {
        c     <- firstCharGen
        extra <- Gen.choose(0, 32)
        cs    <- Gen.listOfN(len - 1 + extra, charGen)
      } yield (c :: cs).mkString

      case LessThen(len) => for {
        c     <- firstCharGen
        len   <- Gen.choose(0, len - 2)
        cs    <- Gen.listOfN(len, charGen)
      } yield (c :: cs).mkString
    }

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Either[String, Gen[T]] = {
      val t = typeOf[T]
      if (typeOf[T] =:= typeOf[String]) Right(g.asInstanceOf[Gen[T]])
      else Left(s"unsupported type ${t.typeSymbol.name.decodedName.toString}; String expected")
    }
  }

  case class identifier(size: SizeDefn) extends StringGenDefn(Gen.alphaLowerChar, Gen.alphaNumChar, size)
  case class numStr(size: SizeDefn) extends StringGenDefn(Gen.numChar, Gen.numChar, size)
  case class alphaUpperStr(size: SizeDefn) extends StringGenDefn(Gen.alphaUpperChar, Gen.alphaUpperChar, size)
  case class alphaLowerStr(size: SizeDefn) extends StringGenDefn(Gen.alphaLowerChar, Gen.alphaLowerChar, size)
  case class alphaStr(size: SizeDefn) extends StringGenDefn(Gen.alphaChar, Gen.alphaChar, size)
  case class alphaNumStr(size: SizeDefn) extends StringGenDefn(Gen.alphaNumChar, Gen.alphaNumChar, size)
  case class asciiStr(size: SizeDefn) extends StringGenDefn(Gen.asciiChar, Gen.asciiChar, size)
  case class asciiPrintableStr(size: SizeDefn) extends StringGenDefn(Gen.asciiPrintableChar, Gen.asciiPrintableChar, size)

  // num
  sealed abstract class NumGenDefn(pos: Boolean) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Either[String, Gen[T]] = typeOf[T] match {
      case t if t =:= typeOf[Byte] => Right((if (pos) Gen.posNum[Byte] else Gen.negNum[Byte]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Short] => Right((if (pos) Gen.posNum[Short] else Gen.negNum[Short]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Int] => Right((if (pos) Gen.posNum[Int] else Gen.negNum[Int]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Long] => Right((if (pos) Gen.posNum[Long] else Gen.negNum[Long]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Float] => Right((if (pos) Gen.posNum[Float] else Gen.negNum[Float]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Double] => Right((if (pos) Gen.posNum[Double] else Gen.negNum[Double]).asInstanceOf[Gen[T]])
      case t => Left(s"unsupported type ${t.typeSymbol.name.decodedName.toString}; one of [Byte, Short, Int, Long, Float, Double] expected")
    }
  }

  case object posNum extends NumGenDefn(true)
  case object negNum extends NumGenDefn(false)

  // const
  case class const(defn: String) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Either[String, Gen[T]] = {
      ForConst.parse[T](defn)
    }
  }

  // range
  case class range(defn: String) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Either[String, Gen[T]] = {
      ForRange.parse[T](defn)
    }
  }

  // oneof
  case class oneof(defn: String) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Either[String, Gen[T]] = {
      ForOneOf.parse[T](defn)
    }
  }

  def parse(defn: String): Either[String, GenDefn] = {
    defn match {
      case "numChar"            => Right(numChar)
      case "alphaUpperChar"     => Right(alphaUpperChar)
      case "alphaLowerChar"     => Right(alphaLowerChar)
      case "alphaChar"          => Right(alphaChar)
      case "alphaNumChar"       => Right(alphaNumChar)
      case "asciiChar"          => Right(asciiChar)
      case "asciiPrintableChar" => Right(asciiPrintableChar)
      case "posNum"             => Right(posNum)
      case "negNum"             => Right(negNum)

      case "identifier"         => Right(identifier(SizeDefn.Free))
      case "numStr"             => Right(numStr(SizeDefn.Free))
      case "alphaUpperStr"      => Right(alphaUpperStr(SizeDefn.Free))
      case "alphaLowerStr"      => Right(alphaLowerStr(SizeDefn.Free))
      case "alphaStr"           => Right(alphaStr(SizeDefn.Free))
      case "alphaNumStr"        => Right(alphaNumStr(SizeDefn.Free))
      case "asciiStr"           => Right(asciiStr(SizeDefn.Free))
      case "asciiPrintableStr"  => Right(asciiPrintableStr(SizeDefn.Free))

      case PrefixedString(prefix, defn) =>
        (prefix, defn) match {
          case ("identifier", _)         => SizeDefn.parse(defn) map identifier.apply
          case ("numStr", _)             => SizeDefn.parse(defn) map numStr.apply
          case ("alphaUpperStr", _)      => SizeDefn.parse(defn) map alphaUpperStr.apply
          case ("alphaLowerStr", _)      => SizeDefn.parse(defn) map alphaLowerStr.apply
          case ("alphaStr", _)           => SizeDefn.parse(defn) map alphaStr.apply
          case ("alphaNumStr", _)        => SizeDefn.parse(defn) map alphaNumStr.apply
          case ("asciiStr", _)           => SizeDefn.parse(defn) map asciiStr.apply
          case ("asciiPrintableStr", _)  => SizeDefn.parse(defn) map asciiPrintableStr.apply

          case ("const", Some(defn))     => Right(const(defn))
          case ("range", Some(defn))     => Right(range(defn))
          case ("oneof", Some(defn))     => Right(oneof(defn))
          case _ => Left(s"can't parse $defn")
        }

      case _ => Left(s"can't parse $defn")
    }
  }
}
