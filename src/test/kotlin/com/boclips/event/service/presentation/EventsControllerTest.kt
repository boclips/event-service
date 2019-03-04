package com.boclips.event.service.presentation

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestMongoProcess
import com.boclips.event.service.testsupport.fakes.FakeMixpanelEventConsumer
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import de.flapdoodle.embed.mongo.MongodProcess
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.litote.kmongo.getCollection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EventsControllerTest : AbstractSpringIntegrationTest() {
    @Test
    fun `it persists the event`() {
        assertThat(getEventsCollection().countDocuments()).isEqualTo(0)
        assertThat(mixpanelEventConsumer.events.size).isEqualTo(0)

        val content = """
            {
                "type": "an-event",
                "properties": {}
            }
        """.trimIndent()

        mockMvc.perform(post("/v1/events").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isAccepted)

        assertThat(getEventsCollection().countDocuments()).isEqualTo(1)
        assertThat(mixpanelEventConsumer.events.size).isEqualTo(1)
    }

    private fun getEventsCollection(): MongoCollection<Document> {
        return mongoClient.getDatabase("event-service-db").getCollection("events")
    }
}