package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.events.Event
import org.apache.spark.rdd.RDD

trait EventLoader {

  def load(): RDD[Event]
}
