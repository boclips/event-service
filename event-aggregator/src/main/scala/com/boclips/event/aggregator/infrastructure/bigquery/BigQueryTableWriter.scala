package com.boclips.event.aggregator.infrastructure.bigquery

import com.boclips.event.aggregator.config.SparkConfig
import com.boclips.event.aggregator.presentation.TableWriter
import com.boclips.event.aggregator.presentation.formatters.schema.Schema
import com.google.cloud.hadoop.io.bigquery.output.{BigQueryOutputConfiguration, IndirectBigQueryOutputFormat}
import com.google.cloud.hadoop.io.bigquery.{BigQueryConfiguration, BigQueryFileFormat}
import com.google.gson.JsonObject
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

class BigQueryTableWriter(private val spark: SparkSession) extends TableWriter {

  private val conf: Configuration = spark.sparkContext.hadoopConfiguration

  private val tmpDirName = TempFolderName()

  conf.set("mapreduce.job.outputformat.class", classOf[IndirectBigQueryOutputFormat[_, _]].getName)
  conf.set(BigQueryConfiguration.OUTPUT_TABLE_WRITE_DISPOSITION_KEY, "WRITE_TRUNCATE")
  conf.set("fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
  conf.set("fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")

  override def writeTable(data: RDD[JsonObject], schema: Schema, tableName: String): Unit = {
    val log: Logger = LoggerFactory.getLogger(classOf[BigQueryTableWriter])
    log.info(s"Writing Big Query table: $tableName")
    val bucket = "boclips-event-aggregator"
    val outputTableId = s"${SparkConfig.PROJECT_ID}:analytics.$tableName"
    val outputGcsPath = s"gs://$bucket/$tmpDirName/$tableName"

    val bigQuerySchema = BigQuerySchema.from(schema)

    BigQueryOutputConfiguration.configure(
      conf,
      outputTableId,
      bigQuerySchema,
      outputGcsPath,
      BigQueryFileFormat.NEWLINE_DELIMITED_JSON,
      classOf[TextOutputFormat[_, _]]
    )

    data
      .map(json => (null, json))
      .saveAsNewAPIHadoopDataset(conf)
    log.info(s"Big Query table written: $tableName")
  }
}
