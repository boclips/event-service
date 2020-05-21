package com.boclips.event.aggregator.domain.service.collection

import com.boclips.event.aggregator.domain.model.events.{CollectionInteractedWithEvent, Event}
import org.apache.spark.rdd.RDD

object CollectionInteractionEventAssembler {

  def apply(events: RDD[_ <: Event]): RDD[CollectionInteractedWithEvent] = {
    events.flatMap {
      case event: CollectionInteractedWithEvent => Some(event)
      case _ => None
    }
  }
}
