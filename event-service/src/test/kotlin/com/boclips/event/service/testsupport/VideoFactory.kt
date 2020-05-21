package com.boclips.event.service.testsupport

import com.boclips.event.service.testsupport.SubjectFactory.createSubjects
import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.video.ContentPartner
import com.boclips.eventbus.domain.video.Dimensions
import com.boclips.eventbus.domain.video.PlaybackProviderType
import com.boclips.eventbus.domain.video.Video
import com.boclips.eventbus.domain.video.VideoAsset
import com.boclips.eventbus.domain.video.VideoId
import com.boclips.eventbus.domain.video.VideoType
import java.time.ZonedDateTime

object VideoFactory {

    fun createVideo(
        id: String = "",
        title: String = "",
        contentPartnerName: String = "",
        playbackProviderType: PlaybackProviderType = PlaybackProviderType.KALTURA,
        subjectNames: List<String> = emptyList(),
        ageRange: AgeRange = AgeRange(),
        durationSeconds: Int = 180,
        ingestedAt: ZonedDateTime = ZonedDateTime.now(),
        type: VideoType = VideoType.INSTRUCTIONAL,
        originalDimensions: Dimensions? = Dimensions(640, 480),
        assets: List<VideoAsset>? = listOf()
    ): Video {
        return Video
            .builder()
            .id(VideoId(id))
            .ingestedAt(ingestedAt)
            .title(title)
            .contentPartner(ContentPartner.of(contentPartnerName))
            .playbackProviderType(playbackProviderType)
            .subjects(createSubjects(subjectNames))
            .ageRange(ageRange)
            .durationSeconds(durationSeconds)
            .type(type)
            .originalDimensions(originalDimensions)
            .assets(assets)
            .build()
    }
}
