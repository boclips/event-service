package com.boclips.event.aggregator.domain.model.collections

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.AgeRange
import com.boclips.event.aggregator.domain.model.users.UserId
import com.boclips.event.aggregator.domain.model.videos.VideoId


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
                       promoted: Boolean,
                       deleted: Boolean,
                     )


