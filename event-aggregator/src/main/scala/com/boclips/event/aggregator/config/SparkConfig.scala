package com.boclips.event.aggregator.config

import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.SparkSession

class SparkConfig(
                   private val googleServiceAccountKeyFile: String,
                   private val numberLocalExecutors: Int
                 ) {
  lazy val session: SparkSession = {
    val spark = SparkSession.builder()
      .config("spark.executor.memory", "6g")
      .config("spark.driver.memory", "4g")
      .master(s"local[$numberLocalExecutors]")
      .appName("EventAggregator")
      .getOrCreate()

    val conf: Configuration = spark.sparkContext.hadoopConfiguration
    conf.set("fs.gs.project.id", SparkConfig.PROJECT_ID)
    conf.set("google.cloud.auth.service.account.enable", "true")
    conf.set("google.cloud.auth.service.account.json.keyfile", googleServiceAccountKeyFile)

    spark
  }
}


object SparkConfig {
  val PROJECT_ID = "boclips-prod"

  def apply(): SparkConfig = {
    val googleServiceAccountKeyFile = Option(System.getenv("GOOGLE_SERVICE_ACCOUNT_KEY"))
      .getOrElse("event-aggregator-google-service-account-key.json")
    new SparkConfig(googleServiceAccountKeyFile, 5)
  }
}
