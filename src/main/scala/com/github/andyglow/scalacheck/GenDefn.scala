package com.github.andyglow.scalacheck

import com.github.andyglow.util.Result
import com.github.andyglow.util.Result._
import org.scalacheck.Gen

import scala.reflect.runtime.universe.{Expr => _, _}


sealed trait GenDefn extends Product {

  def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Result[Gen[T]]
}

object GenDefn {

  // char
  sealed abstract class CharGenDefn(g: Gen[Char]) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Result[Gen[T]] = {
      val t = typeOf[T]
      if (typeOf[T] =:= typeOf[Char]) Ok(g.asInstanceOf[Gen[T]])
      else Error(s"unsupported type ${t.typeSymbol.name.decodedName.toString}; Char expected")
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
  sealed abstract class StringGenDefn(
    firstCharGen: Gen[Char],
    charGen: Gen[Char],
    size: Expr[Int]) extends GenDefn {
    import ExprPackage.IntExprs._

    private val g = size match {
      case LessThen(1) | Exact(0) => Gen const ""
      case Free => for {
        c <- firstCharGen
        cs <- Gen.listOf(charGen)
      } yield (c::cs).mkString

      case Exact(1) | LessThen(2) => firstCharGen map { _.toString }

      case Exact(len) => for {
        c  <- firstCharGen
        cs <- Gen.listOfN(len - 1, charGen)
      } yield (c :: cs).mkString

      case GreaterThen(len) => for {
        c     <- firstCharGen
        extra <- Gen.choose(1, 32)
        cs    <- Gen.listOfN(len - 1 + extra, charGen)
      } yield (c :: cs).mkString

      case LessThen(len) => for {
        c     <- firstCharGen
        len   <- Gen.choose(0, len - 2)
        cs    <- Gen.listOfN(len, charGen)
      } yield (c :: cs).mkString

      case Inside(l, r) => for {
        c     <- firstCharGen
        len   <- Gen.choose(l, r - 2)
        cs    <- Gen.listOfN(len, charGen)
      } yield (c :: cs).mkString

      case Outside(l, r) => for {
        c     <- firstCharGen
        len   <- Gen.oneOf(Gen.choose(0, l - 1), Gen.choose(r, r + 32))
        cs    <- Gen.listOfN(len, charGen)
      } yield (c :: cs).mkString

      case _ => throw new Exception("should never get here")
    }

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Result[Gen[T]] = {
      val t = typeOf[T]
      if (typeOf[T] =:= typeOf[String]) Ok(g.asInstanceOf[Gen[T]])
      else Error(s"unsupported type ${t.typeSymbol.name.decodedName.toString}; String expected")
    }
  }

  case class identifier(size: Expr[Int]) extends StringGenDefn(Gen.alphaLowerChar, Gen.alphaNumChar, size)
  case class numStr(size: Expr[Int]) extends StringGenDefn(Gen.numChar, Gen.numChar, size)
  case class alphaUpperStr(size: Expr[Int]) extends StringGenDefn(Gen.alphaUpperChar, Gen.alphaUpperChar, size)
  case class alphaLowerStr(size: Expr[Int]) extends StringGenDefn(Gen.alphaLowerChar, Gen.alphaLowerChar, size)
  case class alphaStr(size: Expr[Int]) extends StringGenDefn(Gen.alphaChar, Gen.alphaChar, size)
  case class alphaNumStr(size: Expr[Int]) extends StringGenDefn(Gen.alphaNumChar, Gen.alphaNumChar, size)
  case class asciiStr(size: Expr[Int]) extends StringGenDefn(Gen.asciiChar, Gen.asciiChar, size)
  case class asciiPrintableStr(size: Expr[Int]) extends StringGenDefn(Gen.asciiPrintableChar, Gen.asciiPrintableChar, size)

  // num
  sealed abstract class NumGenDefn(pos: Boolean) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Result[Gen[T]] = typeOf[T] match {
      case t if t =:= typeOf[Byte] => Ok((if (pos) Gen.posNum[Byte] else Gen.negNum[Byte]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Short] => Ok((if (pos) Gen.posNum[Short] else Gen.negNum[Short]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Int] => Ok((if (pos) Gen.posNum[Int] else Gen.negNum[Int]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Long] => Ok((if (pos) Gen.posNum[Long] else Gen.negNum[Long]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Float] => Ok((if (pos) Gen.posNum[Float] else Gen.negNum[Float]).asInstanceOf[Gen[T]])
      case t if t =:= typeOf[Double] => Ok((if (pos) Gen.posNum[Double] else Gen.negNum[Double]).asInstanceOf[Gen[T]])
      case t => Error(s"unsupported type ${t.typeSymbol.name.decodedName.toString}; one of [Byte, Short, Int, Long, Float, Double] expected")
    }
  }

  case object posNum extends NumGenDefn(true)
  case object negNum extends NumGenDefn(false)

  // const
  case class const(defn: String) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Result[Gen[T]] = {
      ForConst.parse[T](defn)
    }
  }

  // range
  case class range(min: String, max: String) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Result[Gen[T]] = {
      ForRange.parse[T](min, max)
    }
  }

  // oneof
  case class oneof(x: String, xs: String*) extends GenDefn {

    def gen[T: ForConst: ForRange: ForOneOf: TypeTag]: Result[Gen[T]] = {
      ForOneOf.parse[T](x, xs:_*)
    }
  }
}
