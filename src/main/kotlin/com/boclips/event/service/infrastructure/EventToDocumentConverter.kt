package com.boclips.event.service.infrastructure

import com.boclips.events.types.UserActivated
import com.boclips.events.types.base.Event
import com.boclips.events.types.base.UserEvent
import com.boclips.events.types.video.VideosSearched
import org.bson.Document

object EventToDocumentConverter {

    fun convertUserActivated(userActivated: UserActivated): Document {
        return Document(
                convertUserEvent(userActivated, type = "USER_ACTIVATED")
                        + ("totalUsers" to userActivated.totalUsers)
                        + ("activatedUsers" to userActivated.activatedUsers)
        )
    }

    fun convertVideosSearched(videosSearched: VideosSearched): Document {
        return Document(
                convertUserEvent(videosSearched, type = "VIDEOS_SEARCHED")
                        + ("query" to videosSearched.query)
                        + ("pageIndex" to videosSearched.pageIndex)
                        + ("pageSize" to videosSearched.pageSize)
                        + ("totalResults" to videosSearched.totalResults.toLong())
        )
    }

    private fun <T> convertUserEvent(event: T, type: String): Map<String, Any> where T : UserEvent, T : Event {
        return mapOf<String, Any>(
                "type" to type,
                "userId" to event.user.id,
                "userIsBoclips" to event.user.email.endsWith("@boclips.com"),
                "timestamp" to event.timestamp,
                "url" to event.url
        )
    }

}
