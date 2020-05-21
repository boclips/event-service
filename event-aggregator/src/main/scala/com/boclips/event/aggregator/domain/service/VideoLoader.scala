package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.Video
import org.apache.spark.rdd.RDD

trait VideoLoader {

  def load(): RDD[Video]
}

