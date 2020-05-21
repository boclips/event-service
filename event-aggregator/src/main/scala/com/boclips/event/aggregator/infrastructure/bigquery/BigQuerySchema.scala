package com.boclips.event.aggregator.infrastructure.bigquery

import com.boclips.event.aggregator.presentation.formatters.schema.{BooleanFieldType, BytesFieldType, DateFieldType, DatetimeFieldType, FieldMode, FieldType, FloatFieldType, IntegerFieldType, NullableFieldMode, RecordFieldType, RepeatedFieldMode, RequiredFieldMode, Schema, SchemaField, StringFieldType, StructFieldType, TimeFieldType, TimestampFieldType}
import com.google.cloud.hadoop.io.bigquery.output.{BigQueryTableFieldSchema, BigQueryTableSchema}

import scala.collection.JavaConverters._

object BigQuerySchema {
  def from(schema: Schema): BigQueryTableSchema = {
    val bigQuerySchema = new BigQueryTableSchema()
    bigQuerySchema.setFields(fieldsFrom(schema).asJava)
    bigQuerySchema
  }

  def fieldsFrom(schema: Schema): List[BigQueryTableFieldSchema] = schema.fields.map(toBigQueryField)

  private def toBigQueryField(field: SchemaField): BigQueryTableFieldSchema = {
    val bigQueryField = new BigQueryTableFieldSchema()
    bigQueryField.setName(field.fieldName)
    bigQueryField.setType(toBigQueryFieldType(field.fieldType))
    bigQueryField.setMode(toBigQueryFieldMode(field.fieldMode))
    field.fieldSchema.foreach(fieldSchema =>
      bigQueryField.setFields(fieldsFrom(fieldSchema).asJava)
    )
    bigQueryField
  }

  private def toBigQueryFieldType(fieldType: FieldType) = fieldType match {
    case StringFieldType => "STRING"
    case BytesFieldType => "BYTES"
    case IntegerFieldType => "INTEGER"
    case FloatFieldType => "FLOAT"
    case BooleanFieldType => "BOOLEAN"
    case TimestampFieldType => "TIMESTAMP"
    case DateFieldType => "DATE"
    case TimeFieldType => "TIME"
    case DatetimeFieldType => "DATETIME"
    case RecordFieldType => "STRUCT"
  }

  private def toBigQueryFieldMode(fieldMode: FieldMode) = fieldMode match {
    case NullableFieldMode => "NULLABLE"
    case RequiredFieldMode => "REQUIRED"
    case RepeatedFieldMode => "REPEATED"
  }
}
