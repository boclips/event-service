package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.model.Event
import org.json.JSONObject

object EventToBsonConverter {

    fun convert(event: Event): JSONObject {
        return event.properties.toList().fold(JSONObject()) { json, (key, value) -> json.put(key, value) }
    }
}