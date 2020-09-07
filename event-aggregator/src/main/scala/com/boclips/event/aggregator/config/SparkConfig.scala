package com.boclips.event.aggregator.config

import org.apache.spark.sql.SparkSession

class SparkConfig(private val numberLocalExecutors: Int) {
  lazy val session: SparkSession = {
    SparkSession.builder()
      .master(s"local[$numberLocalExecutors]")
      .config("spark.neo4j.url", Env("NEO4J_BOLT_URI"))
      .config("spark.neo4j.user", Env("NEO4J_USERNAME"))
      .config("spark.neo4j.password", Env("NEO4J_PASSWORD"))
      .appName("EventAggregator")
      .getOrCreate()
  }
}

object SparkConfig {
  def apply(): SparkConfig = new SparkConfig(numberLocalExecutors = 5)
}
