package com.boclips.sample

import org.scalatest.{FlatSpec, Matchers}

class MainTest extends FlatSpec with Matchers {
  it should "work" in {
    Main.whatever() should contain ('!')
  }
}