package com.boclips.event.service.testsupport

import com.boclips.event.service.testsupport.SubjectFactory.createSubjects
import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.category.CategoryWithAncestors
import com.boclips.eventbus.domain.contentpartner.ChannelId
import com.boclips.eventbus.domain.video.*
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

object VideoFactory {

    fun createVideo(
        id: String = "",
        title: String = "",
        channelId: String = "",
        playbackProviderType: PlaybackProviderType = PlaybackProviderType.KALTURA,
        playbackId: String = "",
        subjectNames: List<String> = emptyList(),
        ageRange: AgeRange = AgeRange(),
        durationSeconds: Int = 180,
        ingestedAt: ZonedDateTime = ZonedDateTime.now(),
        releasedOn: LocalDate = LocalDate.now(),
        type: VideoType = VideoType.INSTRUCTIONAL,
        originalDimensions: Dimensions? = Dimensions(640, 480),
        assets: List<VideoAsset>? = emptyList(),
        promoted: Boolean = false,
        topics: List<VideoTopic> = emptyList(),
        keywords: List<String> = emptyList(),
        sourceVideoReference: String? = null,
        deactivated: Boolean? = false,
        hasTranscript: Boolean = false,
        categories: MutableMap<VideoCategorySource,MutableSet<CategoryWithAncestors>> = Collections.emptyMap()
    ): Video {
        return Video
            .builder()
            .id(VideoId(id))
            .ingestedAt(ingestedAt)
            .releasedOn(releasedOn)
            .title(title)
            .channelId(ChannelId(channelId))
            .playbackProviderType(playbackProviderType)
            .playbackId(playbackId)
            .subjects(createSubjects(subjectNames))
            .ageRange(ageRange)
            .durationSeconds(durationSeconds)
            .type(type)
            .types(listOf(type))
            .originalDimensions(originalDimensions)
            .assets(assets)
            .promoted(promoted)
            .topics(topics)
            .keywords(keywords)
            .sourceVideoReference(sourceVideoReference)
            .deactivated(deactivated)
            .hasTranscript(hasTranscript)
            .categories(categories)
            .build()
    }
}
