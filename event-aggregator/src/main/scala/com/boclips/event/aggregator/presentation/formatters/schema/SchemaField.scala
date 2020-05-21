package com.boclips.event.aggregator.presentation.formatters.schema

case class SchemaField(fieldName: String, fieldType: FieldType, fieldMode: FieldMode, fieldSchema: Option[Schema])
