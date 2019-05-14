package com.boclips.event.service.application

import com.boclips.event.service.domain.EventWriter
import com.boclips.events.config.Subscriptions.USER_ACTIVATED
import com.boclips.events.config.Subscriptions.VIDEOS_SEARCHED
import com.boclips.events.types.UserActivated
import com.boclips.events.types.video.VideosSearched
import org.springframework.cloud.stream.annotation.StreamListener

class PersistEvent(private val eventWriter: EventWriter) {

    @StreamListener(USER_ACTIVATED)
    fun userActivated(userActivated: UserActivated) {
        eventWriter.writeUserActivated(userActivated)
    }

    @StreamListener(VIDEOS_SEARCHED)
    fun videosSearched(videosSearched: VideosSearched) {
        eventWriter.writeVideosSearched(videosSearched)
    }
}
