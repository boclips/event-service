package com.boclips.event.aggregator.presentation.formatters.schema

import com.google.gson._

import scala.collection.JavaConverters._

case class Schema(fields: List[SchemaField])

object Schema {

  def fromJson(json: JsonObject): Schema = {
    val fields = json.entrySet()
      .asScala
      .toList
      .map(e => field(e.getKey, e.getValue))
    Schema(fields)
  }

  private def field(name: String, value: JsonElement): SchemaField = {
    value match {
      case primitiveValue: JsonPrimitive => valueField(name, primitiveValue)
      case arrayValue: JsonArray => arrayField(name, arrayValue)
      case objectValue: JsonObject => objectField(name, objectValue, NullableFieldMode)
      case _: JsonNull => throw new IllegalStateException(s"Cannot determine type of field $name: value is null.")
    }
  }

  private def valueField(name: String, value: JsonPrimitive): SchemaField = {
    SchemaField(name, primitiveTypeOf(value), NullableFieldMode, None)
  }

  private def arrayField(name: String, array: JsonArray): SchemaField = {
    val value = array.get(0)
    if (value.isJsonPrimitive)
      SchemaField(name, primitiveTypeOf(value.getAsJsonPrimitive), RepeatedFieldMode, None)
    else
      objectField(name, value.getAsJsonObject, RepeatedFieldMode)
  }

  private def objectField(name: String, value: JsonObject, fieldMode: FieldMode): SchemaField = {
    SchemaField(name, RecordFieldType, fieldMode, Some(Schema.fromJson(value)))
  }

  private val dateFormat = "\\d{4}-\\d{2}-\\d{2}".r
  private val timestampFormat = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*".r

  private def primitiveTypeOf(value: JsonPrimitive): FieldType = {
    if (value.isString) {
      value.getAsString match {
        case dateFormat() => DateFieldType
        case timestampFormat() => TimestampFieldType
        case _ => StringFieldType
      }
    }
    else if(value.isNumber) FloatFieldType
    else if(value.isBoolean) BooleanFieldType
    else throw new RuntimeException(s"Cannot determine schema type for field value $value")
  }

}
