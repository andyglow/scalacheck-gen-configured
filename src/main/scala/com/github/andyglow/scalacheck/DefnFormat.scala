package com.github.andyglow.scalacheck

import com.github.andyglow.scalacheck.format.string.StringDefnFormat
import com.github.andyglow.util.Result


trait DefnFormat[D] {

  def make(x: D): Result[GenDefn]
}

object DefnFormat {

  def apply[T: DefnFormat]: DefnFormat[T] = implicitly

  implicit val stringToDefn: DefnFormat[String] = StringDefnFormat
}