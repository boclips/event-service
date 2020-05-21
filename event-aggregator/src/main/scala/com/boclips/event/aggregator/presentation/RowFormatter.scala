package com.boclips.event.aggregator.presentation

import com.google.gson.JsonObject

trait RowFormatter[T] extends Serializable {

  def formatRows(obj: T): TraversableOnce[JsonObject]
}
