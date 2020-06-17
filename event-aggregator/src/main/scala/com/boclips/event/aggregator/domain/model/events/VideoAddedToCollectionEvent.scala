package com.boclips.event.aggregator.domain.model.events

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users.BoclipsUserIdentity
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.infrastructure.EventFields

case class VideoAddedToCollectionEvent(
                                        timestamp: ZonedDateTime,
                                        userIdentity: BoclipsUserIdentity,
                                        videoId: VideoId,
                                        url: Option[Url],
                                        query: Option[Query]

                                      ) extends Event {
  override val typeName: String = EventFields.Type.VIDEO_ADDED_TO_COLLECTION
  override val subtype: Option[String] = None
}
