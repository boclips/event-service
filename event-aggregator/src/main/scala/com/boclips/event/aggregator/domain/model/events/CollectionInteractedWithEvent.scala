package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users.UserIdentity
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.CollectionInteractionEventsFormatter
import com.boclips.event.infrastructure.EventFields

case class CollectionInteractedWithEvent(
                                          timestamp: ZonedDateTime,
                                          userIdentity: UserIdentity,
                                          collectionId: CollectionId,
                                          subtype: Option[String],
                                          url: Option[Url],
                                          query: Option[Query]
                                        ) extends Event {
  override val typeName: String = EventFields.Type.COLLECTION_INTERACTED_WITH
}

object CollectionInteractedWithEvent {
  implicit val formatter: RowFormatter[CollectionInteractedWithEvent] = CollectionInteractionEventsFormatter
}
