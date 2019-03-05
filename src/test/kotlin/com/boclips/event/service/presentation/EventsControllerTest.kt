package com.boclips.event.service.presentation

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.asUser
import com.mongodb.client.MongoCollection
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
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

    @Test
    fun `it adds user id to events when token is present`() {
        val content = """
            {
                "type": "an-event",
                "properties": {}
            }
        """.trimIndent()

        val userID = "event-creator@example.com"

        mockMvc.perform(post("/v1/events").contentType(MediaType.APPLICATION_JSON).content(content).asUser(userID))
                .andExpect(status().isAccepted)

        assertThat(mixpanelEventConsumer.events.size).isEqualTo(1)

        val event = mixpanelEventConsumer.events.first()

        assertThat(event.userID).isEqualTo(userID)
    }

    private fun getEventsCollection(): MongoCollection<Document> {
        return mongoClient.getDatabase("event-service-db").getCollection("events")
    }
}