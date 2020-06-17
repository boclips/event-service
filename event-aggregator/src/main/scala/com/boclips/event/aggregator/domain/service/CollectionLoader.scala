package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.collections.Collection
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait CollectionLoader {

  def load()(implicit session: SparkSession): RDD[Collection]
}

