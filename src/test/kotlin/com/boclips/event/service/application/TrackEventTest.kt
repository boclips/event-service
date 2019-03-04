package com.boclips.event.service.application

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.infrastructure.MongoEventConsumer
import com.boclips.event.service.infrastructure.mixpanel.MixpanelEventConsumer
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrackEventTest {

    lateinit var mongoConsumer: MongoEventConsumer
    lateinit var mixpanelEventConsumer: MixpanelEventConsumer
    lateinit var subject: TrackEvent

    @BeforeEach
    fun setUp() {
        mongoConsumer = mock()
        mixpanelEventConsumer = mock()
        subject = TrackEvent(mongoConsumer, mixpanelEventConsumer)
    }

    @Test
    fun `forwards the event to mongo`() {
        val event = Event("MY_EVENT", mapOf("key" to "value"))

        subject(event)

        verify(mongoConsumer, times(1)).consumeEvent(event)
    }

    @Test
    fun `forwards the event to mixpanel`() {
        val event = Event("MY_EVENT", mapOf("key" to "value"))

        subject(event)

        verify(mixpanelEventConsumer, times(1)).consumeEvent(eq(event))
    }
}