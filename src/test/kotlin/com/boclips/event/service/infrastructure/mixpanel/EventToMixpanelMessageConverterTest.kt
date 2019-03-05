package com.boclips.event.service.infrastructure.mixpanel

import com.boclips.event.service.testsupport.TestFactories.createEvent
import com.mixpanel.mixpanelapi.MessageBuilder
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EventToMixpanelMessageConverterTest {
    private lateinit var messageBuilder: MessageBuilder

    @BeforeEach
    fun setUp() {
        messageBuilder = spy(MessageBuilder("project-id"))
    }

    @Test
    fun `it includes the user ID when present`() {
        val event = createEvent(userID = "user-id")

        EventToMixpanelMessageConverter().invoke(messageBuilder, event)

        verify(messageBuilder).event(eq("user-id"), any(), any())
    }

    @Test
    fun `is uses a random identifier when the user ID isn't present`() {
        val event = createEvent(userID = null)

        EventToMixpanelMessageConverter().invoke(messageBuilder, event)

        verify(messageBuilder).event(check { assertThat(it).isNotEmpty() }, any(), any())
    }
}