package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.Channel
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait ChannelLoader {
  def load()(implicit session: SparkSession): RDD[Channel]
}
