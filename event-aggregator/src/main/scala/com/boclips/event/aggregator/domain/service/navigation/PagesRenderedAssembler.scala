package com.boclips.event.aggregator.domain.service.navigation

import com.boclips.event.aggregator.domain.model.events.{Event, PageRenderedEvent}
import org.apache.spark.rdd.RDD

class PagesRenderedAssembler(events: RDD[_ <: Event]) {
  def assemblePagesRendered(): RDD[PageRenderedEvent] = {
    events.flatMap{
      case event: PageRenderedEvent => Some(event)
      case _ => None
    }
  }

}
