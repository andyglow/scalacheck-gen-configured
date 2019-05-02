package com.github.andyglow.scalacheck

import org.scalatest.Matchers._
import org.scalatest._


class StringParseGenSpec extends WordSpec {
  import ParseGenSpecSupport._

  "ParseGen" when {

    "String" should {

      "handle identifier" when check(
        "identifier",
        { head => head.isLower shouldBe true; head.isLetterOrDigit shouldBe true },
        { char => char.isLetterOrDigit shouldBe true })

      "handle numStr" when check(
        "numStr",
        { _.isDigit shouldBe true })

      "handle alphaUpperStr" when check(
        "alphaUpperStr",
        { char => char.isUpper shouldBe true; char.isLetter shouldBe true })

      "handle alphaLowerStr" when check(
        "alphaLowerStr",
        { char => char.isLower shouldBe true; char.isLetter shouldBe true })

      "handle alphaNumStr" when check(
        "alphaNumStr",
        { char => char.isLetterOrDigit shouldBe true })

      "handle alphaStr" when check(
        "alphaStr",
        { char => char.isLetter shouldBe true })

      "handle asciiStr" when check(
        "asciiStr",
        { char => char.asInstanceOf[Int] should (be >= 0 and be <= 127) })

      "handle asciiPrintableStr" when check(
        "asciiPrintableStr",
        { char => char.asInstanceOf[Int] should (be >= 32 and be <= 126) })

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

  private def check(name: String, checkChar: Char => Unit): Unit = check(name, checkChar, checkChar)

  private def check(name: String, checkHead: Char => Unit, checkChar: Char => Unit): Unit = {

    "no size defined" in {
      val str = doGen[String](name).value
      str should not be 'empty
      checkHead(str.head)
      str foreach checkChar
    }

    "strict 0 size defined" in {
      val str = doGen[String](s"$name: 0").value
      str shouldBe 'empty
    }

    "strict 1 size defined" in {
      val str = doGen[String](s"$name: 1").value
      str should not be 'empty
      checkHead(str.head)
      str foreach checkChar
    }

    "strict 6 size defined" in {
      val str = doGen[String](s"$name: 6").value
      str should have size 6
      checkHead(str.head)
      str foreach checkChar
    }

    "less then 1 size defined" in {
      val str = doGen[String](s"$name: < 1").value
      str shouldBe empty
    }

    "less then 2 size defined" in {
      val str = doGen[String](s"$name: < 2").value
      str should have size 1
      checkHead(str.head)
    }

    "less then 12 size defined" in {
      val str = doGen[String](s"$name: < 12").value
      str.length should be < 12
      checkHead(str.head)
      str foreach checkChar
    }

    "greater then 12 size defined" in {
      val str = doGen[String](s"$name: > 3").value
      str.length should be > 3
      checkHead(str.head)
      str foreach checkChar
    }
  }
}

