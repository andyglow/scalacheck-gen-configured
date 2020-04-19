package com.github.andyglow.scalacheck


trait Expr[T] {
  type Args
  def v: Args
}