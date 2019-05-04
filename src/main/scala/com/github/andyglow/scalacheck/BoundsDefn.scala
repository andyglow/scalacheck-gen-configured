package com.github.andyglow.scalacheck


sealed trait BoundsDefn[T]

class BoundsDefnPkg[T: Numeric: Ordering] {

  case class Exact(value: T) extends BoundsDefn[T]

  case class GreaterThen(value: T) extends BoundsDefn[T]

  case class LessThen(value: T) extends BoundsDefn[T]

  case class Inside(left: T, right: T) extends BoundsDefn[T]

  case class Outside(left: T, right: T) extends BoundsDefn[T]

  case object Free extends BoundsDefn[T]
}

object BoundsDefnPkg {

  implicit val byte: BoundsDefnPkg[Byte] = new BoundsDefnPkg[Byte]
  implicit val short: BoundsDefnPkg[Short] = new BoundsDefnPkg[Short]
  implicit val int: BoundsDefnPkg[Int] = new BoundsDefnPkg[Int]
  implicit val long: BoundsDefnPkg[Long] = new BoundsDefnPkg[Long]
  implicit val float: BoundsDefnPkg[Float] = new BoundsDefnPkg[Float]
  implicit val double: BoundsDefnPkg[Double] = new BoundsDefnPkg[Double]
}