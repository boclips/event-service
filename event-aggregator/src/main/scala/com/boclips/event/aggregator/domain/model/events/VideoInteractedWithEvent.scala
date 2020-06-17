package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users.UserIdentity
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.infrastructure.EventFields

case class VideoInteractedWithEvent(
                                     timestamp: ZonedDateTime,
                                     userIdentity: UserIdentity,
                                     videoId: VideoId,
                                     url: Option[Url],
                                     query: Option[Query],
                                     subtype: Option[String],
                                   ) extends Event {
  override val typeName: String = EventFields.Type.VIDEO_INTERACTED_WITH
}
