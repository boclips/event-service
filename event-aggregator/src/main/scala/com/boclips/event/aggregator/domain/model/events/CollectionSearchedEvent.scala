package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users.UserIdentity

case class CollectionSearchedEvent
(
  timestamp: ZonedDateTime,
  userIdentity: UserIdentity,
  query: Query,
  url: Option[Url],
  collectionResults: Iterable[CollectionId],
  pageIndex: Int,
  pageSize: Int,
  totalResults: Int
) extends Event {
  override val typeName: String = "COLLECTION_SEARCH"
  override val subtype: Option[String] = None
}
