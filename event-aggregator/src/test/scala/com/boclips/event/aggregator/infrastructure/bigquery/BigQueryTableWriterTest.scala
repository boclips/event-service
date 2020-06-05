package com.boclips.event.aggregator.infrastructure.bigquery

import java.io.FileInputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

import com.boclips.event.aggregator.config.{BigQueryConfig, Env}
import com.boclips.event.aggregator.presentation.formatters.schema.{NullableFieldMode, Schema, SchemaField, StringFieldType}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.google.cloud.bigquery.QueryJobConfiguration
import com.google.gson.JsonObject
import com.google.cloud.bigquery.JobId
import java.util.UUID

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.bigquery.JobInfo
import com.google.cloud.bigquery.BigQueryOptions

import scala.collection.JavaConverters._


class BigQueryTableWriterTest extends IntegrationTest {

  it should "write data in the table" in sparkTest { implicit spark =>
    val config = BigQueryConfig()
    val writer = new BigQueryTableWriter(config)

    val schema = Schema(SchemaField(
      fieldName = "fieldName",
      fieldMode = NullableFieldMode,
      fieldType = StringFieldType,
      fieldSchema = None,
    ) :: Nil)

    val uniqueValue = ZonedDateTime.now().format(ISO_OFFSET_DATE_TIME)

    val data = rdd(uniqueValue).map { value =>
      val row = new JsonObject()
      row.addProperty("fieldName", value)
      row
    }

    writer.writeTable(data, schema, "test_table")

    val queryConfig = QueryJobConfiguration
      .newBuilder(s"SELECT * FROM `${config.projectId}.${config.dataset}.test_table`")
      .setUseLegacySql(false)
      .build()
    val jobId = JobId.of(UUID.randomUUID.toString)
    val bigquery = BigQueryOptions.newBuilder()
      .setCredentials(
        ServiceAccountCredentials.fromStream(new FileInputStream(config.serviceAccountKeyPath.toFile))
      )
      .build()
      .getService
    val queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build).waitFor()

    queryJob should not be null
    queryJob.getStatus.getError shouldBe null
    val results = queryJob.getQueryResults().iterateAll().asScala.toList
    results should have size 1
    results.head.get("fieldName").getStringValue shouldBe uniqueValue
  }

}
