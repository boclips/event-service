package com.boclips.event.aggregator.config

import org.apache.spark.sql.SparkSession

class SparkConfig(private val numberLocalExecutors: Int) {
  lazy val session: SparkSession = {
    SparkSession.builder()
      .master(s"local[$numberLocalExecutors]")
      .appName("EventAggregator")
      .getOrCreate()
  }
}


object SparkConfig {
  def apply(): SparkConfig = new SparkConfig(numberLocalExecutors = 5)
}
