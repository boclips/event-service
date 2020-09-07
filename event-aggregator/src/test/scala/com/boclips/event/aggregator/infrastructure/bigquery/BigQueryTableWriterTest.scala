package com.boclips.event.aggregator.infrastructure.bigquery

import java.io.FileInputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.util.UUID

import com.boclips.event.aggregator.config.BigQueryConfig
import com.boclips.event.aggregator.presentation.formatters.schema.{NullableFieldMode, Schema, SchemaField, StringFieldType}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.bigquery._
import com.google.gson.JsonObject
import org.scalatest.BeforeAndAfterEach

import scala.collection.JavaConverters._
import scala.util.Random


class BigQueryTableWriterTest extends IntegrationTest with BeforeAndAfterEach {

  val config: BigQueryConfig = BigQueryConfig()

  val tableName = s"test_table_${Random.nextInt(Int.MaxValue)}"

  it should "write data in the table" in sparkTest { implicit spark =>
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

    writer.writeTable(data, schema, tableName)

    val results = run(s"SELECT * FROM `${config.projectId}.${config.dataset}.$tableName`")
      .getQueryResults()
      .iterateAll().asScala.toList
    results should have size 1
    results.head.get("fieldName").getStringValue shouldBe uniqueValue
  }

  override def afterEach() {
    run(s"DROP TABLE `${config.projectId}.${config.dataset}.$tableName`")
  }

  private def run(queryString: String): Job = {
    val queryConfig = QueryJobConfiguration
      .newBuilder(queryString)
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
    queryJob
  }

}
