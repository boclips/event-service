package com.boclips.event.aggregator.config

import org.apache.spark.sql.SparkSession

case class SparkConfig(numberLocalExecutors: Int = 5) {
  lazy val session: SparkSession =
    SparkSession.builder()
      .master(s"local[$numberLocalExecutors]")
      .appName("EventAggregator")
      .getOrCreate()
}
