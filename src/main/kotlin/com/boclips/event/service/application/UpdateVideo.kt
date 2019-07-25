package com.boclips.event.service.application

import com.boclips.event.service.domain.VideoRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.events.video.VideoUpdated

class UpdateVideo(private val videoRepository: VideoRepository) {

    @BoclipsEventListener
    fun videoUpdated(event: VideoUpdated) {
        videoRepository.saveVideo(event.video.id.value, event.video.title, event.video.contentPartner.name)
    }
}
