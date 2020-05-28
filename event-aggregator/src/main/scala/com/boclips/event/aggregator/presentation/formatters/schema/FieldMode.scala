package com.boclips.event.aggregator.presentation.formatters.schema

trait FieldMode

case object NullableFieldMode extends FieldMode

case object RequiredFieldMode extends FieldMode

case object RepeatedFieldMode extends FieldMode
