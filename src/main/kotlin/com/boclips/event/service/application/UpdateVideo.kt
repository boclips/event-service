package com.boclips.event.service.application

import com.boclips.event.service.domain.VideoRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.domain.video.Video
import com.boclips.eventbus.events.video.VideoCreated
import com.boclips.eventbus.events.video.VideoUpdated

class UpdateVideo(private val videoRepository: VideoRepository) {

    @BoclipsEventListener
    fun videoUpdated(event: VideoUpdated) {
        saveVideo(event.video)
    }

    @BoclipsEventListener
    fun videoCreated(event: VideoCreated) {
        saveVideo(event.video)
    }

    private fun saveVideo(video: Video) {
        videoRepository.saveVideo(video)
    }
}
