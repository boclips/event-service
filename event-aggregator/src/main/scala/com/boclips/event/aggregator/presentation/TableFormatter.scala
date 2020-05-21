package com.boclips.event.aggregator.presentation

import com.boclips.event.aggregator.presentation.formatters.schema.Schema
import com.boclips.event.aggregator.presentation.formatters.schema.base.ExampleInstance
import com.google.gson.JsonObject
import org.apache.spark.rdd.RDD

import scala.reflect.runtime.universe.TypeTag

class TableFormatter[T](val formatter: RowFormatter[T])(implicit val typeTag: TypeTag[T]) {

  def formatRowsAsJson(data: RDD[T]): RDD[JsonObject] = {
    val jsonFormatter = this.formatter
    data.flatMap(jsonFormatter.formatRows)
  }

  def schema(): Schema = Schema.fromJson(formatter.formatRows(ExampleInstance.create[T]()).toList.head)
}
