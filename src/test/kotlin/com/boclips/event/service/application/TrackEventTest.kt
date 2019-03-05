package com.boclips.event.service.application

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.infrastructure.MongoEventConsumer
import com.boclips.event.service.infrastructure.mixpanel.MixpanelEventConsumer
import com.boclips.event.service.presentation.EventResource
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrackEventTest {

    lateinit var mongoConsumer: MongoEventConsumer
    lateinit var mixpanelEventConsumer: MixpanelEventConsumer
    lateinit var trackEvent: TrackEvent

    @BeforeEach
    fun setUp() {
        mongoConsumer = mock()
        mixpanelEventConsumer = mock()
        trackEvent = TrackEvent(mongoConsumer, mixpanelEventConsumer)
    }

    @Test
    fun `forwards the event to mongo`() {
        val event = EventResource(type = "MY_EVENT", properties = mapOf("key" to "value"))

        trackEvent(event)

        verify(mongoConsumer, times(1)).consumeEvent(
                Event(type = event.type, properties = event.properties, userID = null)
        )
    }

    @Test
    fun `forwards the event to mixpanel`() {
        val event = EventResource(type = "MY_EVENT", properties = mapOf("key" to "value"))

        trackEvent(event)

        verify(mixpanelEventConsumer, times(1)).consumeEvent(
                Event(type = event.type, properties = event.properties, userID = null)
        )
    }
}