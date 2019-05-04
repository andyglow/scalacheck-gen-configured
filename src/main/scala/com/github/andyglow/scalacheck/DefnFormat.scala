package com.github.andyglow.scalacheck

import com.github.andyglow.scalacheck.format.string.StringDefnFormat

import scala.reflect.runtime.universe._


trait DefnFormat[D] {

  def make(x: D): Either[String, GenDefn]
}

object DefnFormat {

  def apply[T: DefnFormat]: DefnFormat[T] = implicitly

  implicit val stringToDefn: DefnFormat[String] = StringDefnFormat
}