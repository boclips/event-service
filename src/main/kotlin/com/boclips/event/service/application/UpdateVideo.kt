package com.boclips.event.service.application

import com.boclips.event.service.domain.VideoRepository
import com.boclips.events.config.Subscriptions.*
import com.boclips.events.types.video.VideoUpdated

import org.springframework.cloud.stream.annotation.StreamListener

class UpdateVideo(private val videoRepository: VideoRepository) {

    @StreamListener(VIDEO_UPDATED)
    fun videoUpdated(event: VideoUpdated) {
        videoRepository.saveVideo(event.videoId, event.title, event.contentPartnerName)
    }
}
