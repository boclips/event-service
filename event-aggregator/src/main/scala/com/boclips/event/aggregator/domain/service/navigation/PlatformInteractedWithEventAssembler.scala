package com.boclips.event.aggregator.domain.service.navigation

import com.boclips.event.aggregator.domain.model.events.{Event, PlatformInteractedWithEvent}
import org.apache.spark.rdd.RDD

class PlatformInteractedWithEventAssembler(events: RDD[_ <: Event]) {
  def assemblePlatformInteractedWithEvents(): RDD[PlatformInteractedWithEvent] = {
    events.flatMap {
      case event: PlatformInteractedWithEvent => Some(event)
      case _ => None
    }
  }

}
