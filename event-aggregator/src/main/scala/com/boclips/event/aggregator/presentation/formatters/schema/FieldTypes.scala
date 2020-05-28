package com.boclips.event.aggregator.presentation.formatters.schema

trait FieldType

case object StringFieldType extends FieldType

case object BytesFieldType extends FieldType

case object IntegerFieldType extends FieldType

case object FloatFieldType extends FieldType

case object BooleanFieldType extends FieldType

case object TimestampFieldType extends FieldType

case object DateFieldType extends FieldType

case object TimeFieldType extends FieldType

case object DatetimeFieldType extends FieldType

case object RecordFieldType extends FieldType

case object StructFieldType extends FieldType
