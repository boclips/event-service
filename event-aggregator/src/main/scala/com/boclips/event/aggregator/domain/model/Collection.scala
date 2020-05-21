package com.boclips.event.aggregator.domain.model

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.events.CollectionInteractedWithEvent

case class CollectionId(value: String) extends Ordered[CollectionId] {

  override def compare(that: CollectionId): Int = value.compare(that.value)
}

case class Collection(
                       id: CollectionId,
                       title: String,
                       description: String,
                       subjects: List[String],
                       ageRange: AgeRange,
                       videoIds: List[VideoId],
                       ownerId: UserId,
                       bookmarks: List[UserId],
                       createdTime: ZonedDateTime,
                       updatedTime: ZonedDateTime,
                       public: Boolean,
                       deleted: Boolean
                     )

case class CollectionWithRelatedData(collection: Collection, impressions: List[CollectionSearchResultImpression], interactions: List[CollectionInteractedWithEvent])
