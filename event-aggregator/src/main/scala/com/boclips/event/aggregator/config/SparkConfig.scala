package com.boclips.event.aggregator.config

import org.apache.spark.sql.SparkSession

case class SparkConfig(
                        neo4jConfig: Option[Neo4jConfig],
                        numberLocalExecutors: Int = 5
                      ) {
  lazy val session: SparkSession = {

    var builder = SparkSession.builder()
      .master(s"local[$numberLocalExecutors]")

    builder = neo4jConfig.map(config =>
      builder
        .config("spark.neo4j.url", config.boltUri)
        .config("spark.neo4j.user", config.username)
        .config("spark.neo4j.password", config.password)
    ).getOrElse(builder)

    builder
      .appName("EventAggregator")
      .getOrCreate()
  }
}
