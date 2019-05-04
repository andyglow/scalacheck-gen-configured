package com.github.andyglow.scalacheck

import com.github.andyglow.scalacheck.format.string.BoundsParser
import org.scalatest._
import org.scalatest.Matchers._


class BoundsParserSpec extends WordSpec {

  "BoundsParser" should {

    "parse[Int]" when {
      import BoundsDefnPkg.int._

      "no input" in {
        BoundsParser[Int].parse(None) shouldBe Right(Free)
        BoundsParser[Int].parse(Some(null)) shouldBe Right(Free)
        BoundsParser[Int].parse(Some("")) shouldBe Right(Free)
      }

      "exact" in {
        BoundsParser[Int].parse("0") shouldBe Right(Exact(0))
        BoundsParser[Int].parse("5") shouldBe Right(Exact(5))
      }

      "greater then" in {
        BoundsParser[Int].parse("> 0") shouldBe Right(GreaterThen(0))
        BoundsParser[Int].parse("> 18") shouldBe Right(GreaterThen(18))
      }

      "less then" in {
        BoundsParser[Int].parse("< 18") shouldBe Right(LessThen(18))
      }

      "exact negative" in {
        BoundsParser[Int].parse("-5") shouldBe Right(Exact(-5))
      }

      "greater then negative" in {
        BoundsParser[Int].parse("> -5") shouldBe Right(GreaterThen(-5))
      }

      "less then negative" in {
        BoundsParser[Int].parse("< -5") shouldBe Right(LessThen(-5))
      }

      "less then 0" in {
        BoundsParser[Int].parse("< 0") shouldBe Right(LessThen(0))
      }

      "less then 0 and less then 3" in {
        BoundsParser[Int].parse("< 0 < 3") shouldBe Right(LessThen(3))
      }

      "greater then 0 and greater then 3" in {
        BoundsParser[Int].parse("> 0 > 3") shouldBe Right(GreaterThen(0))
      }

      "greater then 0 and less then 3" in {
        BoundsParser[Int].parse("> 0 < 3") shouldBe Right(Inside(0, 3))
      }

      "less then 0 and greater then 3" in {
        BoundsParser[Int].parse("< 0 > 3") shouldBe Right(Outside(0, 3))
      }
    }

    "parse[Double]" when {
      import BoundsDefnPkg.double._

      "exact" in {
        BoundsParser[Double].parse("0") shouldBe Right(Exact(0))
        BoundsParser[Double].parse(".5") shouldBe Right(Exact(.5))
        BoundsParser[Double].parse("0.5") shouldBe Right(Exact(.5))
        BoundsParser[Double].parse("-0.5") shouldBe Right(Exact(-.5))
        BoundsParser[Double].parse("-.5") shouldBe Right(Exact(-.5))
      }
    }
  }
}
