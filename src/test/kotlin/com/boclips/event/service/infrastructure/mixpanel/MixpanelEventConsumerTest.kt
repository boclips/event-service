package com.boclips.event.service.infrastructure.mixpanel

import com.boclips.event.service.domain.model.Event
import com.mixpanel.mixpanelapi.MixpanelAPI
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
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
        val event = Event(type = "a-type", properties = mapOf("prop1" to "val1"), userID = null)

        mixpanelEventConsumer.consumeEvent(event)

        verify(mixpanelAPI, times(1)).deliver(any())
    }
}