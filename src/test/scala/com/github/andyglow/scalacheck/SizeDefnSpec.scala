package com.github.andyglow.scalacheck

import org.scalatest._
import org.scalatest.Matchers._


class SizeDefnSpec extends WordSpec {
  import SizeDefn._

  "SizeDefn" should {

    "parse" when {

      "no input" in {
        SizeDefn.parse(None) shouldBe Right(Free)
        SizeDefn.parse(Some(null)) shouldBe Right(Free)
        SizeDefn.parse(Some("")) shouldBe Right(Free)
      }

      "strict" in {
        SizeDefn.parse("0") shouldBe Right(Strict(0))
        SizeDefn.parse("5") shouldBe Right(Strict(5))
      }

      "greated then" in {
        SizeDefn.parse("> 0") shouldBe Right(GreaterThen(0))
        SizeDefn.parse("> 18") shouldBe Right(GreaterThen(18))
      }

      "less then" in {
        SizeDefn.parse("< 18") shouldBe Right(LessThen(18))
      }
    }

    "not parse" when {

      "size is negative" in {
        SizeDefn.parse("-5") shouldBe 'left
        SizeDefn.parse("> -5") shouldBe 'left
        SizeDefn.parse("< -5") shouldBe 'left
      }

      "less then 0" in {
        SizeDefn.parse("< 0") shouldBe 'left
      }
    }
  }
}
