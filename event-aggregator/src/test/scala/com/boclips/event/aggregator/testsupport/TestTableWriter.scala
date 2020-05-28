package com.boclips.event.aggregator.testsupport

import com.boclips.event.aggregator.presentation.TableWriter
import com.boclips.event.aggregator.presentation.formatters.schema.Schema
import com.google.gson.JsonObject
import org.apache.spark.rdd.RDD

class TestTableWriter extends TableWriter {
  override def writeTable(data: RDD[JsonObject], schema: Schema, tableName: String): Unit = {

  }
}
