package com.github.andyglow.scalacheck

import com.github.andyglow.util.Result._
import com.github.andyglow.scalacheck.format.string.ExprParser
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import com.github.andyglow.util.ScalaVersionSpecificPackage._


class ExprParserSpec extends AnyWordSpec {

  "BoundsParser" should {

    "parse[Int]" when {
      import ExprPackage.IntExprs._

      "no input" in {
        ExprParser[Int].parse(None) shouldBe Ok(Free)
        ExprParser[Int].parse(Some(null)) shouldBe Ok(Free)
        ExprParser[Int].parse(Some("")) shouldBe Ok(Free)
      }

      "exact" in {
        ExprParser[Int].parse("0") shouldBe Ok(mkExact(0))
        ExprParser[Int].parse("5") shouldBe Ok(mkExact(5))
      }

      "greater then" in {
        ExprParser[Int].parse("> 0") shouldBe Ok(mkGreaterThen(0))
        ExprParser[Int].parse("> 18") shouldBe Ok(mkGreaterThen(18))
      }

      "less then" in {
        ExprParser[Int].parse("< 18") shouldBe Ok(mkLessThen(18))
      }

      "exact negative" in {
        ExprParser[Int].parse("-5") shouldBe Ok(mkExact(-5))
      }

      "greater then negative" in {
        ExprParser[Int].parse("> -5") shouldBe Ok(mkGreaterThen(-5))
      }

      "less then negative" in {
        ExprParser[Int].parse("< -5") shouldBe Ok(mkLessThen(-5))
      }

      "less then 0" in {
        ExprParser[Int].parse("< 0") shouldBe Ok(mkLessThen(0))
      }

      "less then 0 and less then 3" in {
        ExprParser[Int].parse("< 0 < 3") shouldBe Ok(mkLessThen(3))
      }

      "greater then 0 and greater then 3" in {
        ExprParser[Int].parse("> 0 > 3") shouldBe Ok(mkGreaterThen(0))
      }

      "greater then 0 and less then 3" in {
        ExprParser[Int].parse("> 0 < 3") shouldBe Ok(mkInside(0, 3))
      }

      "less then 0 and greater then 3" in {
        ExprParser[Int].parse("< 0 > 3") shouldBe Ok(mkOutside(0, 3))
      }
    }

    "parse[Double]" when {
      import ExprPackage.DoubleExprs._

      "exact" in {
        ExprParser[Double].parse("0") shouldBe Ok(mkExact(0))
        ExprParser[Double].parse(".5") shouldBe Ok(mkExact(.5))
        ExprParser[Double].parse("0.5") shouldBe Ok(mkExact(.5))
        ExprParser[Double].parse("-0.5") shouldBe Ok(mkExact(-.5))
        ExprParser[Double].parse("-.5") shouldBe Ok(mkExact(-.5))
      }
    }
  }
}
