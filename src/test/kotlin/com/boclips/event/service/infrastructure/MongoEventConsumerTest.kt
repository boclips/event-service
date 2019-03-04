package com.boclips.event.service.infrastructure

import com.boclips.event.service.domain.model.Event
import com.boclips.event.service.infrastructure.MongoEventConsumer.Companion.COLLECTION_NAME
import com.boclips.event.service.infrastructure.MongoEventConsumer.Companion.DB_NAME
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestMongoProcess
import de.flapdoodle.embed.mongo.MongodProcess
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.findOne

class MongoEventConsumerTest : AbstractSpringIntegrationTest() {
    @Test
    fun `it persists the event`() {
        val consumer = MongoEventConsumer(mongoClient)
        val event = Event("a-type", mapOf("prop1" to "val1"))

        consumer.consumeEvent(event)

        val collection = getEventsCollection()

        assertThat(collection.countDocuments()).isEqualTo(1)

        val persistedEvent = collection.findOne()!!

        assertThat(persistedEvent.getString("type")).isEqualTo("a-type")
        assertThat(persistedEvent.getString("prop1")).isEqualTo("val1")
    }

    private fun getEventsCollection() = mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
}