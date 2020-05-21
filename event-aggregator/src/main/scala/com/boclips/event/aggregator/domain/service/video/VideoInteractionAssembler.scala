package com.boclips.event.aggregator.domain.service.video

import com.boclips.event.aggregator.domain.model.events.{Event, VideoInteractedWithEvent}
import org.apache.spark.rdd.RDD

object VideoInteractionAssembler {

  def apply(events: RDD[_ <: Event]): RDD[VideoInteractedWithEvent] = {
    events.flatMap {
      case event: VideoInteractedWithEvent => Some(event)
      case _ => None
    }
  }
}
