package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.{Collection, Video}
import org.apache.spark.rdd.RDD

trait CollectionLoader {

  def load(): RDD[Collection]
}

