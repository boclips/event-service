package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.Channel
import org.apache.spark.rdd.RDD

trait ChannelLoader {
  def load(): RDD[Channel]
}
