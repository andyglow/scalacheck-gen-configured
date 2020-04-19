package com.github.andyglow.scalacheck

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec


class PrefixedStringSpec extends AnyWordSpec {
  import PrefixedStringSpec._

  "PrefixedString" should {

    "handle known prefixes" when {

      "no RHS defined" in {
        instance.unapply("foo") shouldBe Some(("foo", None))
        instance.unapply(" foo") shouldBe Some(("foo", None))
        instance.unapply(" foo ") shouldBe Some(("foo", None))
        instance.unapply("foo ") shouldBe Some(("foo", None))
      }

      "RHS is defined" in {
        instance.unapply("foo:bar") shouldBe Some(("foo", Some("bar")))
        instance.unapply(" foo: bar") shouldBe Some(("foo", Some("bar")))
        instance.unapply(" foo :  bar") shouldBe Some(("foo", Some("bar")))
        instance.unapply("foo:bar  ") shouldBe Some(("foo", Some("bar")))
      }
    }

    "not handle unknown prefixes" when {

      "no RHS defined" in {
        instance.unapply("baz") shouldBe None
        instance.unapply(" baz") shouldBe None
        instance.unapply(" baz ") shouldBe None
        instance.unapply("baz ") shouldBe None
      }

      "RHS is defined" in {
        instance.unapply("baz:bar") shouldBe None
        instance.unapply(" baz: bar") shouldBe None
        instance.unapply(" baz :  bar") shouldBe None
        instance.unapply("baz:bar  ") shouldBe None
      }
    }
  }
}

object PrefixedStringSpec {

  val instance = new PrefixedString(':', Set("foo", "bar"))
}