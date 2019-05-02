package com.github.andyglow.scalacheck

import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import org.scalactic.source
import org.scalatest._
import org.scalatest.Matchers._
import org.scalatest.EitherValues._

import scala.reflect.runtime.universe._
import scala.util.Success


class ParseGenSpec extends WordSpec {
  import ParseGenSpec._

  "ParseGen" when {

    "Int" should {

      "handle posNum" in { doGen[Int]("posNum").value should be >= 0 }

      "handle negNum" in { doGen[Int]("negNum").value should be <= 0 }

      "handle const" in {
        doGen[Int]("const: 15").value shouldBe 15
        doGen[Int]("const: -15").value shouldBe -15
      }

      "handle range" in {
        doGen[Int]("range: 5..7").value should (be >= 5 and be <= 7)
      }

      "handle oneof" in {
        doGen[Int]("oneof: 1,2,3").value should (be >= 1 and be <= 3)
      }

      for { dfn <- List(
        "numChar",
        "alphaUpperChar",
        "alphaLowerChar",
        "alphaChar",
        "alphaNumChar",
        "asciiChar",
        "asciiPrintableChar",
        "identifier",
        "numStr",
        "alphaUpperStr",
        "alphaLowerStr",
        "alphaStr",
        "alphaNumStr",
        "asciiStr",
        "asciiPrintableStr") }
        s"not handle $dfn" in { doGen[Int](dfn) shouldBe 'left }
    }

    "Long" should {

      "handle posNum" in { doGen[Long]("posNum").value should be >= 0l }

      "handle negNum" in { doGen[Long]("negNum").value should be <= 0l }

      "handle const" in {
        doGen[Long]("const: 15").value shouldBe 15l
        doGen[Long]("const: -15").value shouldBe -15l
      }

      "handle range" in {
        doGen[Long]("range: 5..7").value should (be >= 5l and be <= 7l)
      }

      "handle oneof" in {
        doGen[Long]("oneof: 1,2,3").value should (be >= 1l and be <= 3l)
      }

      for { dfn <- List(
        "numChar",
        "alphaUpperChar",
        "alphaLowerChar",
        "alphaChar",
        "alphaNumChar",
        "asciiChar",
        "asciiPrintableChar",
        "identifier",
        "numStr",
        "alphaUpperStr",
        "alphaLowerStr",
        "alphaStr",
        "alphaNumStr",
        "asciiStr",
        "asciiPrintableStr") }
        s"not handle $dfn" in { doGen[Long](dfn) shouldBe 'left }
    }

    "Double" should {

      "handle posNum" in { doGen[Double]("posNum").value should be >= .0 }

      "handle negNum" in { doGen[Double]("negNum").value should be <= .0 }

      "handle const" in {
        doGen[Double]("const: 15.658").value shouldBe 15.658
        doGen[Double]("const: -0.0002").value shouldBe -0.0002
      }

      "handle range" in {
        doGen[Double]("range: 0.5..0.7").value should (be >= .5 and be <= .7)
      }

      "handle oneof" in {
        doGen[Double]("oneof: 0.1, 0.2, 0.3").value should (be >= .1 and be <= .3)
      }

      for { dfn <- List(
        "numChar",
        "alphaUpperChar",
        "alphaLowerChar",
        "alphaChar",
        "alphaNumChar",
        "asciiChar",
        "asciiPrintableChar",
        "identifier",
        "numStr",
        "alphaUpperStr",
        "alphaLowerStr",
        "alphaStr",
        "alphaNumStr",
        "asciiStr",
        "asciiPrintableStr") }
        s"not handle $dfn" in { doGen[Double](dfn) shouldBe 'left }
    }

    "String" should {

      "handle identifier" in {
        val str = doGen[String]("identifier").value
        str should not be 'empty
        str.head.isLower shouldBe true
        str foreach { ch =>
          ch.isLetterOrDigit shouldBe true
        }
      }

      "handle numStr" in {
        val str = doGen[String]("numStr").value
        str should not be 'empty
        str foreach { ch =>
          ch.isDigit shouldBe true
        }
      }

      "handle alphaUpperStr" in {
        val str = doGen[String]("alphaUpperStr").value
        str should not be 'empty
        str foreach { ch =>
          ch.isUpper shouldBe true
          ch.isLetter shouldBe true
        }
      }

      "handle alphaLowerStr" in {
        val str = doGen[String]("alphaLowerStr").value
        str should not be 'empty
        str foreach { ch =>
          ch.isLower shouldBe true
          ch.isLetter shouldBe true
        }
      }

      "handle alphaNumStr" in {
        val str = doGen[String]("alphaNumStr").value
        str should not be 'empty
        str foreach { ch =>
          ch.isLetterOrDigit shouldBe true
        }
      }

      "handle alphaStr" in {
        val str = doGen[String]("alphaStr").value
        str should not be 'empty
        str foreach { ch =>
          ch.isLetter shouldBe true
        }
      }

      "handle asciiStr" in {
        val str = doGen[String]("asciiStr").value
        str should not be 'empty
        str foreach { ch =>
          ch.asInstanceOf[Int] should (be >= 0 and be <= 127)
        }
      }

      "handle asciiPrintableStr" in {
        val str = doGen[String]("asciiPrintableStr").value
        str should not be 'empty
        str foreach { ch =>
          ch.asInstanceOf[Int] should (be >= 32 and be <= 126)
        }
      }

      "handle const" in {
        doGen[String]("const: foo").value shouldBe "foo"
      }

      "handle oneof" in {
        doGen[String]("oneof: abc, def, ghi").value should (be("abc") or (be("def") or be("ghi")))
      }

      for { dfn <- List(
        "numChar",
        "alphaUpperChar",
        "alphaLowerChar",
        "alphaChar",
        "alphaNumChar",
        "asciiChar",
        "asciiPrintableChar",
        "posNum",
        "negNum",
        "range: 1 .. 3") }
        s"not handle $dfn" in { doGen[String](dfn) shouldBe 'left }
    }
  }
}

object ParseGenSpec {

  def doGen[T: ForConst: ForRange: ForOneOf: TypeTag](dfn: String): Either[String, T] = {
    ParseGen[T](dfn).right map { _.pureApply(Gen.Parameters.default, Seed.random) }
  }

  implicit class EitherOps[T](private val e: Either[String, T]) extends AnyVal {

    def value(implicit pos: source.Position): T = e match {
      case Right(value) => value
      case Left(error) => fail(error); null.asInstanceOf[T]
    }
  }
}