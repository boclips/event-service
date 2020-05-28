package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._

case class VideoInteractedWithEvent(
                                     timestamp: ZonedDateTime,
                                     userId: UserId,
                                     videoId: VideoId,
                                     url: Option[Url],
                                     query: Option[Query],
                                     subtype: Option[String],
                                   ) extends Event {
  override val deviceId: Option[DeviceId] = None
  override val typeName: String = EventConstants.VIDEO_INTERACTED_WITH
}
