package com.boclips.event.aggregator.infrastructure.bigquery

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

import com.boclips.event.aggregator.config.BigQueryConfig
import com.boclips.event.aggregator.presentation.formatters.schema.{NullableFieldMode, Schema, SchemaField, StringFieldType}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.google.gson.JsonObject
import org.scalatest.Ignore

@Ignore
class BigQueryTableWriterTest extends IntegrationTest {

  it should "write data in the table" in sparkTest { implicit spark =>
    val writer = new BigQueryTableWriter(BigQueryConfig())

    val schema = Schema(SchemaField(
      fieldName = "fieldName",
      fieldMode = NullableFieldMode,
      fieldType = StringFieldType,
      fieldSchema = None,
    ) :: Nil)

    val uniqueValue = ZonedDateTime.now().format(ISO_OFFSET_DATE_TIME)

    val row = new JsonObject()
    row.addProperty("fieldName", uniqueValue)

    writer.writeTable(rdd(row), schema, "test_table")


    val savedRows = spark.read.format("bigquery")
      .option("table","publicdata.samples.shakespeare")
      .load()
      .cache()
      .rdd
      .collect()

    savedRows.length shouldBe 1
  }

}
