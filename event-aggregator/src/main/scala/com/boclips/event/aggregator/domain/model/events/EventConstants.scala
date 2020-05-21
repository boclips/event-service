package com.boclips.event.aggregator.domain.model.events

import com.boclips.event.aggregator.domain.model.UserId

object EventConstants {
  final val anonymousUserId: UserId = UserId("anonymousUser")
  val SEARCH = "SEARCH"
  val VIDEOS_SEARCHED = "VIDEOS_SEARCHED"
  val RESOURCES_SEARCHED = "RESOURCES_SEARCHED"
  val PLAYBACK = "PLAYBACK"
  val VIDEO_SEGMENT_PLAYED = "VIDEO_SEGMENT_PLAYED"
  val VIDEO_INTERACTED_WITH = "VIDEO_INTERACTED_WITH"
  val VIDEO_ADDED_TO_COLLECTION = "VIDEO_ADDED_TO_COLLECTION"
  val PAGE_RENDERED = "PAGE_RENDERED"
  val COLLECTION_INTERACTED_WITH = "COLLECTION_INTERACTED_WITH"


}
