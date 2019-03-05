package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.TestFactories.createEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventToBsonConverterTest {

    @Test
    fun `converts text properties`() {
        val event = createEvent(properties = mapOf("text property" to "value"))
        val bson = EventToBsonConverter.convert(event)

        assertThat(bson.getString("text property")).isEqualTo("value")
    }

    @Test
    fun `converts numeric properties`() {
        val event = createEvent(properties = mapOf("numeric property" to 10))
        val bson = EventToBsonConverter.convert(event)

        assertThat(bson.getInt("numeric property")).isEqualTo(10)
    }


}