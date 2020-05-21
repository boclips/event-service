package com.boclips.event.aggregator.config

import com.boclips.event.aggregator.infrastructure.mongo.MongoConnectionDetails
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.SparkSession

class SparkConfig(
                   private val inputConnectionDetails: MongoConnectionDetails,
                   private val googleServiceAccountKeyFile: String,
                   private val numberLocalExecutors: Int
                 ) {
  lazy val session: SparkSession = {
    val spark = SparkSession.builder()
      .master(s"local[$numberLocalExecutors]")
      .appName("EventAggregator")
      .config("spark.mongodb.input.uri", inputConnectionDetails.getConnectionUri())
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
    val mongoHost = env("MONGO_HOST")
    val mongoInputDb = env("MONGO_INPUT_DATABASE")
    val mongoOptions = env("MONGO_OPTIONS")

    val googleServiceAccountKeyFile = Option(System.getenv("GOOGLE_SERVICE_ACCOUNT_KEY"))
      .getOrElse("event-aggregator-google-service-account-key.json")

    val inputConnectionDetails = MongoConnectionDetails(
      mongoHost,
      mongoInputDb,
      Option(mongoOptions)
    )

    new SparkConfig(inputConnectionDetails, googleServiceAccountKeyFile, 5)
  }

  private def env(name: String): String = {
    val value = System.getenv(name)
    if (value == null) {
      throw new IllegalStateException(s"$name is not set")
    }
    value
  }
}
