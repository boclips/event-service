package com.boclips.event.aggregator.infrastructure.bigquery

import com.boclips.event.aggregator.config.BigQueryConfig
import com.boclips.event.aggregator.presentation.TableWriter
import com.boclips.event.aggregator.presentation.formatters.schema.Schema
import com.google.cloud.hadoop.io.bigquery.output.{BigQueryOutputConfiguration, IndirectBigQueryOutputFormat}
import com.google.cloud.hadoop.io.bigquery.{BigQueryConfiguration, BigQueryFileFormat}
import com.google.gson.JsonObject
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.slf4j.{Logger, LoggerFactory}

class BigQueryTableWriter(private val config: BigQueryConfig) extends TableWriter {

  override def writeTable(data: RDD[JsonObject], schema: Schema, tableName: String): Unit = {
    val log: Logger = LoggerFactory.getLogger(classOf[BigQueryTableWriter])
    val outputTableId = s"${config.projectId}:${config.dataset}.$tableName"
    val outputGcsPath = s"gs://${config.bucket}/${config.tmpDirName}/$tableName"
    log.info(s"Writing Big Query table: $tableName via $outputGcsPath")
    val conf = setup(data.sparkContext)

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

  private def setup(context: SparkContext): Configuration = {
    val conf = context.hadoopConfiguration
    conf.set("mapreduce.job.outputformat.class", classOf[IndirectBigQueryOutputFormat[_, _]].getName)
    conf.set(BigQueryConfiguration.OUTPUT_TABLE_WRITE_DISPOSITION.getKey, "WRITE_TRUNCATE")
    conf.set("fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
    conf.set("fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
    conf.set("fs.gs.project.id", config.projectId)
    conf.set("google.cloud.auth.service.account.enable", "true")
    conf.set("google.cloud.auth.service.account.json.keyfile", config.serviceAccountKeyPath.toFile.getPath)
    conf
  }
}
