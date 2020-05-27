package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.Order
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait OrderLoader {

  def load()(implicit session: SparkSession): RDD[Order]
}
