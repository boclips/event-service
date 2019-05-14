package com.boclips.event.service.application

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createUserActivated
import com.boclips.event.service.testsupport.TestFactories.createVideosSearched
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder

class PersistEventIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun userActivated() {
        val event = createUserActivated(userId = "user-id")

        subscriptions.userActivated().send(msg(event))

        assertThat(document().toJson()).contains("user-id")
    }

    @Test
    fun videosSearched() {
        val event = createVideosSearched(query = "hi")

        subscriptions.videosSearched().send(msg(event))

        assertThat(document().toJson()).contains("hi")
    }

    private fun document(): Document {
        return mongoClient.getDatabase("video-service-db").getCollection("event-log").find().toList().single()
    }

    private fun <T> msg(payload: T): Message<T> {
        return MessageBuilder.withPayload(payload).build()
    }
}
