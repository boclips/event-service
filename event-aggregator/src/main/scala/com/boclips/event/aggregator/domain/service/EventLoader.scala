package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.events.Event
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait EventLoader {

  def load()(implicit session: SparkSession): RDD[Event]
}
