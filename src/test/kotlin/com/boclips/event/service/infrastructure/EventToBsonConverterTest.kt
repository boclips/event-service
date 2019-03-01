package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.model.Event
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventToBsonConverterTest {

    @Test
    fun `converts text properties`() {
        val bson = EventToBsonConverter.convert(Event("SOME_EVENT_TYPE", mapOf(
                "text property" to "value")
        ))

        assertThat(bson.getString("text property")).isEqualTo("value")
    }

    @Test
    fun `converts numeric properites`() {
        val bson = EventToBsonConverter.convert(Event("SOME_EVENT_TYPE", mapOf(
                        "numeric property" to 10)
        ))

        assertThat(bson.getInt("numeric property")).isEqualTo(10)
    }


}