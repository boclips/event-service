package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.contentpackages.ContentPackage
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

trait ContentPackageLoader {
  def load()(implicit session: SparkSession): RDD[ContentPackage]
}
