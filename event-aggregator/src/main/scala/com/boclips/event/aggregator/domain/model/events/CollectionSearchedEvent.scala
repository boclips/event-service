package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{CollectionId, DeviceId, Query, Url, UserId}

case class CollectionSearchedEvent
(
  timestamp: ZonedDateTime,
  userId: UserId,
  query: Query,
  url: Option[Url],
  collectionResults: Iterable[CollectionId],
  pageIndex: Int,
  pageSize: Int,
  totalResults: Int
) extends Event {
  override val deviceId: Option[DeviceId] = None
  override val typeName: String = "COLLECTION_SEARCH"
  override val subtype: Option[String] = None
}