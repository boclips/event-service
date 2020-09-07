package com.boclips.event.aggregator.testsupport.testfactories

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.collections.{Collection, CollectionId}
import com.boclips.event.aggregator.domain.model.users.UserId
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.domain.model.{collections, _}

object CollectionFactory {

  def createCollection(
                        id: String = "123",
                        title: String = "title",
                        description: String = "description",
                        subjects: List[String] = List(),
                        ageRange: AgeRange = AgeRange(None, None),
                        videoIds: List[String] = List(),
                        ownerId: String = "owner",
                        bookmarks: List[String] = List(),
                        createdTime: ZonedDateTime = ZonedDateTime.now(),
                        updatedTime: ZonedDateTime = ZonedDateTime.now(),
                        deleted: Boolean = false,
                        public: Boolean = true,
                        promoted: Boolean = false
                      ): Collection =
    collections.Collection(
      id = CollectionId(id),
      title = title,
      description = description,
      subjects = subjects,
      ageRange = ageRange,
      videoIds = videoIds.map(VideoId),
      ownerId = UserId(ownerId),
      bookmarks = bookmarks.map(UserId),
      createdTime = createdTime,
      updatedTime = updatedTime,
      public = public,
      deleted = deleted,
      promoted = promoted,
    )
}
