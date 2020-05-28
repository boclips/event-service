package com.boclips.event.aggregator.presentation.formatters.schema

import java.time.{LocalDate, ZonedDateTime}

import com.boclips.event.aggregator.presentation.formatters._
import com.boclips.event.aggregator.testsupport.Test
import com.google.gson.{JsonNull, JsonObject, JsonPrimitive}

class SchemaTest extends Test {

  var json: JsonObject = _

  override def beforeEach(): Unit = {
    json = new JsonObject()
  }

  it should "handle string fields" in {
    json.addProperty("stringField", "string value")

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("stringField", StringFieldType, NullableFieldMode, None)
  }

  it should "handle date fields" in {
    json.addDateProperty("date", LocalDate.now())

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("date", DateFieldType, NullableFieldMode, None)
  }

  it should "handle timestamp fields" in {
    json.addDateTimeProperty("timestamp", ZonedDateTime.now())

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("timestamp", TimestampFieldType, NullableFieldMode, None)
  }

  it should "handle number fields" in {
    json.addProperty("numberField", 10.0)

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("numberField", FloatFieldType, NullableFieldMode, None)
  }

  it should "handle boolean fields" in {
    json.addProperty("booleanField", true)

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("booleanField", BooleanFieldType, NullableFieldMode, None)
  }

  it should "handle string array fields" in {
    json.addJsonArrayProperty("stringArray", List(new JsonPrimitive("")))

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("stringArray", StringFieldType, RepeatedFieldMode, None)
  }

  it should "handle numeric array fields" in {
    json.addJsonArrayProperty("numericArray", List(new JsonPrimitive(10)))

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("numericArray", FloatFieldType, RepeatedFieldMode, None)
  }

  it should "handle object fields" in {
    val jsonObject = new JsonObject
    jsonObject.addJsonArrayProperty("stringArray", List(new JsonPrimitive("")))
    json.add("object", jsonObject)

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("object", RecordFieldType, NullableFieldMode, Some(Schema(
      List(SchemaField("stringArray", StringFieldType, RepeatedFieldMode, None))
    )))
  }

  it should "handle object array fields" in {
    val jsonObject = new JsonObject
    jsonObject.addJsonArrayProperty("numericArray", List(new JsonPrimitive(0)))
    json.addJsonArrayProperty("objectArray", List(jsonObject))

    val schema = Schema.fromJson(json)

    schema.fields should contain only SchemaField("objectArray", RecordFieldType, RepeatedFieldMode, Some(Schema(
      List(SchemaField("numericArray", FloatFieldType, RepeatedFieldMode, None))
    )))
  }

  it should "throw on null fields" in {
    json.add("nullField", JsonNull.INSTANCE)

    assertThrows[IllegalStateException] {
      Schema.fromJson(json)
    }
  }
}
