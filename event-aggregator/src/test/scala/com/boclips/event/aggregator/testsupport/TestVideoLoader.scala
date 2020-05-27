package com.boclips.event.aggregator.testsupport

import com.boclips.event.aggregator.domain.model.Video
import com.boclips.event.aggregator.domain.service.VideoLoader
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class TestVideoLoader(private val videos: Seq[Video])(implicit spark: SparkSession) extends VideoLoader {

  override def load()(implicit session: SparkSession): RDD[Video] = {
    spark.sparkContext.parallelize(videos)
  }
}
