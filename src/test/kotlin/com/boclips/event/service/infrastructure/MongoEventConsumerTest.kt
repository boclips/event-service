package com.boclips.event.service.infrastructure

import com.boclips.event.service.infrastructure.MongoEventConsumer.Companion.COLLECTION_NAME
import com.boclips.event.service.infrastructure.MongoEventConsumer.Companion.DB_NAME
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.litote.kmongo.findOne

class MongoEventConsumerTest : AbstractSpringIntegrationTest() {
    @Test
    fun `it persists the event`() {
        val consumer = MongoEventConsumer(mongoClient)
        val event = createEvent(type = "a-type", properties = mapOf("prop1" to "val1"), userID = "user-id")

        consumer.consumeEvent(event)

        val collection = getEventsCollection()

        assertThat(collection.countDocuments()).isEqualTo(1)

        val persistedEvent = collection.findOne()!!

        assertThat(persistedEvent.getString("type")).isEqualTo("a-type")
        assertThat(persistedEvent.getString("prop1")).isEqualTo("val1")
        assertThat(persistedEvent.getString("userId")).isEqualTo("user-id")
    }

    private fun getEventsCollection() = mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
}