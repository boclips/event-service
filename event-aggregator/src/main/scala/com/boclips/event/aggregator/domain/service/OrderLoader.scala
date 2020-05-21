package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.Order
import org.apache.spark.rdd.RDD

trait OrderLoader {

  def load(): RDD[Order]
}
