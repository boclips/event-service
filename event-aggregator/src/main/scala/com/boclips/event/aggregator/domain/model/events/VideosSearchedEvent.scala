package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users.UserIdentity
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.infrastructure.EventFields

case class VideosSearchedEvent
(
  timestamp: ZonedDateTime,
  userIdentity: UserIdentity,
  query: Query,
  url: Option[Url],
  videoResults: Option[Iterable[VideoId]],
  pageIndex: Int,
  totalResults: Int
) extends Event {
  override val typeName: String = EventFields.Type.VIDEOS_SEARCHED
  override val subtype: Option[String] = None
}


