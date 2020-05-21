package com.boclips.sample

import com.boclips.SharedResource

object Main {
  def main(args: Array[String]): Unit = {
    println(whatever())
  }

  def whatever(): String = {
    new SharedResource(3)
    "Yooooooo!"
  }
}
