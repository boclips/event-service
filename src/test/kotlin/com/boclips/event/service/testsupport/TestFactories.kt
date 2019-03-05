package com.boclips.event.service.testsupport

import com.boclips.event.service.domain.model.Event

object TestFactories {
    fun createEvent(
            type: String = "event-type",
            properties: Map<String, Any> = mapOf("prop1" to "val1"),
            userID: String? = null
    ): Event {
        return Event(
                type = type,
                properties = properties,
                userID = userID
        )
    }
}