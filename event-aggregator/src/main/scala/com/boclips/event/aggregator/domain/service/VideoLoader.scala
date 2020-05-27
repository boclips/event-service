package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.Video
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait VideoLoader {

  def load()(implicit session: SparkSession): RDD[Video]
}

