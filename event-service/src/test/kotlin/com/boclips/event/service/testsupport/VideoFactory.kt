package com.boclips.event.service.testsupport

import com.boclips.event.service.testsupport.SubjectFactory.createSubjects
import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.contentpartner.ChannelId
import com.boclips.eventbus.domain.video.Dimensions
import com.boclips.eventbus.domain.video.PlaybackProviderType
import com.boclips.eventbus.domain.video.Video
import com.boclips.eventbus.domain.video.VideoAsset
import com.boclips.eventbus.domain.video.VideoId
import com.boclips.eventbus.domain.video.VideoTopic
import com.boclips.eventbus.domain.video.VideoType
import java.time.LocalDate
import java.time.ZonedDateTime

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
        assets: List<VideoAsset>? = listOf(),
        promoted: Boolean = false,
        topics: List<VideoTopic> = listOf()
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
            .build()
    }
}
