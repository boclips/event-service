package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{DeviceId, Query, Url, UserId, VideoId}

case class VideoAddedToCollectionEvent (
                                         timestamp: ZonedDateTime,
                                         userId: UserId,
                                         videoId: VideoId,
                                         url: Option[Url],
                                         query: Option[Query]

) extends Event {
  override val deviceId: Option[DeviceId] = None
  override val typeName: String = EventConstants.VIDEO_ADDED_TO_COLLECTION
  override val subtype: Option[String] = None
}
