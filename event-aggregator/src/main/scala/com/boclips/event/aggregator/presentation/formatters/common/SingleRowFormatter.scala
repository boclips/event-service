package com.boclips.event.aggregator.presentation.formatters.common

import com.boclips.event.aggregator.presentation.RowFormatter
import com.google.gson.JsonObject

abstract class SingleRowFormatter[T] extends RowFormatter[T] {

  override def formatRows(obj: T): TraversableOnce[JsonObject] = Some(formatRow(obj))

  def formatRow(obj: T): JsonObject = {
    val json = new JsonObject
    writeRow(obj, json)
    json
  }

  def extendRow(obj: T, existingJson: JsonObject): JsonObject = {
    val json = existingJson
    writeRow(obj, json)
    json
  }

  def writeRow(obj: T, json: JsonObject): Unit

}
