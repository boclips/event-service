package com.boclips.event.aggregator.presentation

import com.boclips.event.aggregator.presentation.formatters.schema.Schema
import com.google.gson.JsonObject
import org.apache.spark.rdd.RDD

trait TableWriter {
  def writeTable(data: RDD[JsonObject], schema: Schema, tableName: String): Unit
}
