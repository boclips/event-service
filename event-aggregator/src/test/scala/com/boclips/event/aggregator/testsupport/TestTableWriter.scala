package com.boclips.event.aggregator.testsupport

import com.boclips.event.aggregator.presentation.TableWriter
import com.boclips.event.aggregator.presentation.formatters.schema.Schema
import com.google.gson.{JsonObject, JsonParser}
import org.apache.spark.rdd.RDD

import scala.collection.mutable

class TestTableWriter extends TableWriter {

  private val tableByName = mutable.Map[String, List[String]]()

  override def writeTable(data: RDD[JsonObject], schema: Schema, tableName: String): Unit = {
    tableByName.+=((tableName, data.map(_.toString).collect().toList))
  }

  def table(tableName: String): Option[List[JsonObject]] = {
    tableByName.get(tableName).map(items => items.map(jsonStr => JsonParser.parseString(jsonStr).getAsJsonObject))
  }
}
