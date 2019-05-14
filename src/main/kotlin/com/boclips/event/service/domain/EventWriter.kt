package com.boclips.event.service.domain

import com.boclips.events.types.UserActivated
import com.boclips.events.types.video.VideosSearched

interface EventWriter {
    fun writeUserActivated(userActivated: UserActivated)
    fun writeVideosSearched(videosSearched: VideosSearched)
}
