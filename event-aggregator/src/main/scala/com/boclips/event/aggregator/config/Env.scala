package com.boclips.event.aggregator.config

object Env {

  def apply(name: String): String = {
    val value = System.getenv(name)
    if (value == null) {
      throw new IllegalStateException(s"$name is not set")
    }
    value
  }

}
