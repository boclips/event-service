package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.model.Event
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventToBsonConverterTest {

    @Test
    fun `converts text properties`() {
        val bson = EventToBsonConverter.convert(Event(
                type = "SOME_EVENT_TYPE",
                properties = mapOf("text property" to "value"),
                userID = null
        ))

        assertThat(bson.getString("text property")).isEqualTo("value")
    }

    @Test
    fun `converts numeric properites`() {
        val bson = EventToBsonConverter.convert(Event(
                type = "SOME_EVENT_TYPE",
                properties = mapOf("numeric property" to 10),
                userID = null
        ))

        assertThat(bson.getInt("numeric property")).isEqualTo(10)
    }


}