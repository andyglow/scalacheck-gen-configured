package com.github.andyglow.scalacheck

import scala.collection.immutable.HashSet


private[scalacheck] class PrefixedString(
  separatorChar: Char = PrefixedString.separatorChar,
  knownPrefixes: Set[String] = PrefixedString.knownPrefixes) {

  def unapply(str: String): Option[(String, Option[String])] = {
    val x               = str.trim
    val separatorIndex  = x.indexOf(separatorChar.asInstanceOf[Int])
    val prefix          = (if (separatorIndex > 0) x.substring(0, separatorIndex) else x).trim

    if (!(knownPrefixes contains prefix)) None else {
      if (separatorIndex <= 0) Some((prefix, None)) else {
        Some((prefix, Some(x.substring(separatorIndex + 1).trim)))
      }
    }
  }
}

private[scalacheck] object PrefixedString {

  val separatorChar: Char = ':'

  val knownPrefixes: Set[String] = HashSet(
    "const",
    "range",
    "oneof",
    "identifier",
    "numStr",
    "alphaUpperStr",
    "alphaLowerStr",
    "alphaStr",
    "alphaNumStr",
    "asciiStr",
    "asciiPrintableStr")

  private val default = new PrefixedString

  def unapply(x: String): Option[(String, Option[String])] = default unapply x
}