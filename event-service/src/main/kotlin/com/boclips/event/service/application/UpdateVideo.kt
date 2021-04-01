package com.boclips.event.service.application

import com.boclips.event.service.domain.VideoRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.domain.video.Video
import com.boclips.eventbus.events.video.VideoBroadcastRequested
import com.boclips.eventbus.events.video.VideoCreated
import com.boclips.eventbus.events.video.VideoUpdated
import mu.KLogging

class UpdateVideo(private val videoRepository: VideoRepository) {

    @BoclipsEventListener
    fun videoUpdated(event: VideoUpdated) {
        saveVideo(event.video)
    }

    @BoclipsEventListener
    fun videoCreated(event: VideoCreated) {
        saveVideo(event.video)
    }

    @BoclipsEventListener
    fun videoBroadcastRequested(event: VideoBroadcastRequested) {
        logger.info {
            "Processing VideoBroadcastRequested event for video with ID ${event.video.id}." +
                " Source video reference: ${event.video.sourceVideoReference}"
        }
        saveVideo(event.video)
        logger.info {
            "Processing VideoBroadcastRequested event for video with ID (${event.video.id}) finished."
        }
    }

    private fun saveVideo(video: Video) {
        videoRepository.saveVideo(video)
    }

    companion object : KLogging()
}
