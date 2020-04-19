import sbt._

object Boiler {

  val header = "// auto-generated"

  /*
    implicit val byte: BoundsDefnPkg[Byte] = new BoundsDefnPkg[Byte]
    implicit val short: BoundsDefnPkg[Short] = new BoundsDefnPkg[Short]
    implicit val int: BoundsDefnPkg[Int] = new BoundsDefnPkg[Int]
    implicit val long: BoundsDefnPkg[Long] = new BoundsDefnPkg[Long]
    implicit val float: BoundsDefnPkg[Float] = new BoundsDefnPkg[Float]
    implicit val double: BoundsDefnPkg[Double] = new BoundsDefnPkg[Double]
   */

  def gen(dir: File, scalaVersion: String): Seq[File] = {
    val templates = List(
      new Template("Byte"),
      new Template("Short"),
      new Template("Int"),
      new Template("Long"),
      new Template("Float"),
      new Template("Double"))

    val file = dir / "com" / "github" / "andyglow" / "scalacheck" / "ExprPackage.scala"

    val isGreaterOrEqualThen213 = CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, minor)) if minor >= 13 => true
      case _                               => false
    }

//    val isGreaterOrEqualThen213 = false

    val body =
      s"""package com.github.andyglow.scalacheck
         |
         |abstract class ExprPackage[T: Numeric: Ordering] {
         |
         |  type Exact >: Null <: Expr[T] { type Args = T }
         |  def mkExact(v: T): Exact
         |  val Exact: ExactExtractor
         |  trait ExactExtractor {
         |    def unapply(in: Expr[T]): Option[T]
         |  }
         |
         |  type GreaterThen >: Null <: Expr[T] { type Args = T }
         |  def mkGreaterThen(v: T): GreaterThen
         |  val GreaterThen: GreaterThenExtractor
         |  trait GreaterThenExtractor {
         |    def unapply(in: Expr[T]): Option[T]
         |  }
         |
         |  type LessThen >: Null <: Expr[T] { type Args = T }
         |  def mkLessThen(v: T): LessThen
         |  val LessThen: LessThenExtractor
         |  trait LessThenExtractor {
         |    def unapply(in: Expr[T]): Option[T]
         |  }
         |
         |  type Inside >: Null <: Expr[T] { type Args = (T, T) }
         |  def mkInside(l: T, r: T): Inside
         |  val Inside: InsideExtractor
         |  trait InsideExtractor {
         |    def unapply(in: Expr[T]): Option[(T, T)]
         |  }
         |
         |  type Outside >: Null <: Expr[T] { type Args = (T, T) }
         |  def mkOutside(l: T, r: T): Outside
         |  val Outside: OutsideExtractor
         |  trait OutsideExtractor {
         |    def unapply(in: Expr[T]): Option[(T, T)]
         |  }
         |
         |  type Free >: Null <: Expr[T] { type Args = Null }
         |  val Free: Free
         |}
         |
         |object ExprPackage {
         |  ${if(isGreaterOrEqualThen213)
             """private implicit def dblOrd: Ordering[Double] = Ordering.Double.TotalOrdering
               |private implicit def fltOrd: Ordering[Float] = Ordering.Float.TotalOrdering
               |""".stripMargin else ""}
         |
         |
         |
         |${ templates.map { _.body }.mkString("\n\n\t") }
         |}
         |""".stripMargin

    Seq {
      IO.write(file, body)
      file
    }
  }

  class Template(T: String) {

    def body: String = {
      s"""implicit object ${T}Exprs extends ExprPackage[$T] {
         |
         |    final case class ExactC(v: $T) extends Expr[$T] { type Args = $T }
         |    override type Exact = ExactC
         |    override def mkExact(v: $T): Exact = ExactC(v)
         |    override val Exact: ExactExtractor = new ExactExtractor {
         |      override def unapply(in: Expr[$T]): Option[$T] = in match {
         |        case c: ExactC => Some(c.v)
         |        case _         => None
         |      }
         |    }
         |
         |    final case class GreaterThenC(v: $T) extends Expr[$T] { type Args = $T }
         |    override type GreaterThen = GreaterThenC
         |    override def mkGreaterThen(v: $T): GreaterThen = GreaterThenC(v)
         |    override val GreaterThen: GreaterThenExtractor = new GreaterThenExtractor {
         |      override def unapply(in: Expr[$T]): Option[$T] = in match {
         |        case c: GreaterThenC => Some(c.v)
         |        case _               => None
         |      }
         |    }
         |
         |    final case class LessThenC(v: $T) extends Expr[$T] { type Args = $T }
         |    override type LessThen = LessThenC
         |    override def mkLessThen(v: $T): LessThen = LessThenC(v)
         |    override val LessThen: LessThenExtractor = new LessThenExtractor {
         |      override def unapply(in: Expr[$T]): Option[$T] = in match {
         |        case c: LessThenC => Some(c.v)
         |        case _            => None
         |      }
         |    }
         |
         |    final case class InsideC(l: $T, r: $T) extends Expr[$T] { type Args = ($T, $T); def v: Args = (l, r) }
         |    override type Inside = InsideC
         |    override def mkInside(l: $T, r: $T): Inside = InsideC(l, r)
         |    override val Inside: InsideExtractor = new InsideExtractor {
         |      override def unapply(in: Expr[$T]): Option[($T, $T)] = in match {
         |        case c: InsideC => Some(c.v)
         |        case _          => None
         |      }
         |    }
         |
         |    final case class OutsideC(l: $T, r: $T) extends Expr[$T] { type Args = ($T, $T); def v: Args = (l, r) }
         |    override type Outside = OutsideC
         |    override def mkOutside(l: $T, r: $T): Outside = OutsideC(l, r)
         |    override val Outside: OutsideExtractor = new OutsideExtractor {
         |      override def unapply(in: Expr[$T]): Option[($T, $T)] = in match {
         |        case c: OutsideC => Some(c.v)
         |        case _           => None
         |      }
         |    }
         |
         |    final case object FreeC extends Expr[$T] { type Args = Null; def v: Args = null }
         |    override type Free = FreeC.type
         |    override val Free: Free = FreeC
         |}
         |""".stripMargin
    }
  }
}
