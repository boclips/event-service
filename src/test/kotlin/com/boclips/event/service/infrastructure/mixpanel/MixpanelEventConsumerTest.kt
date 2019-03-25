package com.boclips.event.service.infrastructure.mixpanel

import com.boclips.event.service.testsupport.TestFactories.createEvent
import com.mixpanel.mixpanelapi.MixpanelAPI
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MixpanelEventConsumerTest {

    lateinit var mixpanelAPI: MixpanelAPI
    lateinit var mixpanelEventConsumer: MixpanelEventConsumer

    @BeforeEach
    fun setup() {
        mixpanelAPI = mock()
        mixpanelEventConsumer = MixpanelEventConsumer(mixpanel = mixpanelAPI, projectToken = "")
    }

    @Test
    fun `sends the event to Mixpanel`() {
        val event = createEvent()

        mixpanelEventConsumer.consumeEvent(event)

        verify(mixpanelAPI, times(1)).deliver(any(), any())
    }

    @Test
    fun `indicates the server's IP should not be recorded as the user's location`() {
        val event = createEvent()

        mixpanelEventConsumer.consumeEvent(event)

        verify(mixpanelAPI, times(1)).deliver(any(), eq(false))
    }
}