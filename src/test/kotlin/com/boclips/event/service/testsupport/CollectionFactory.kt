package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.collection.Collection
import com.boclips.eventbus.domain.collection.CollectionId
import com.boclips.eventbus.domain.user.UserId
import com.boclips.eventbus.domain.video.VideoId
import java.time.ZonedDateTime

object CollectionFactory {

    fun createCollection(
        id: String = "",
        title: String = "",
        description: String = "",
        subjects: Set<String> = emptySet(),
        ageRange: AgeRange = AgeRange(null, null),
        videoIds: List<String> = emptyList(),
        ownerId: String = "",
        createdTime: ZonedDateTime = ZonedDateTime.now(),
        updatedTime: ZonedDateTime = ZonedDateTime.now(),
        bookmarks: List<String> = emptyList(),
        isPublic: Boolean = true
    ): Collection {
        return Collection.builder()
            .id(CollectionId(id))
            .createdAt(createdTime)
            .updatedAt(updatedTime)
            .title(title)
            .description(description)
            .subjects(subjects.map { Subject(SubjectId("$it-id"), it) })
            .ageRange(ageRange)
            .videosIds(videoIds.map { VideoId(it) })
            .ownerId(UserId(ownerId))
            .bookmarks(bookmarks.map { UserId(it) })
            .isPublic(isPublic)
            .build()
    }
}
