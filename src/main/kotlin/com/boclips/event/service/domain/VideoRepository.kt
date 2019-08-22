package com.boclips.event.service.domain

import com.boclips.eventbus.domain.video.Video

interface VideoRepository {
    fun saveVideo(video: Video)
}
