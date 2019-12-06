package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories
import com.boclips.eventbus.domain.video.VideoId
import com.boclips.eventbus.events.order.OrderItem
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.ZonedDateTime

class MongoOrderRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var orderRepository: MongoOrderRepository

    @Test
    fun `creating a order`() {
        orderRepository.saveOrder(TestFactories.createOrder(
                id = "123",
                createdAt = ZonedDateTime.parse("2019-10-01T00:00:00Z"),
                updatedAt = ZonedDateTime.parse("2020-11-01T00:00:00Z"),
                customerOrganisationName = "pearson",
                items = listOf(
                  OrderItem.builder().priceGbp(BigDecimal("10.50")).videoId(VideoId("the-video-id")).build()
                )
        ))

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("123")
        assertThat(document.getDate("createdAt")).isEqualTo("2019-10-01T00:00:00Z")
        assertThat(document.getDate("updatedAt")).isEqualTo("2020-11-01T00:00:00Z")
        assertThat(document.getString("customerOrganisationName")).isEqualTo("pearson")
        assertThat(document.get("items", List::class.java).first()).isEqualTo(Document(mapOf("videoId" to "the-video-id", "priceGbp" to "10.50")))
    }

    @Test
    fun `updating a order`() {
        orderRepository.saveOrder(TestFactories.createOrder(id = "1234", updatedAt = ZonedDateTime.parse("2019-10-01T00:00:00Z")))
        orderRepository.saveOrder(TestFactories.createOrder(id = "1234", updatedAt = ZonedDateTime.parse("2020-10-01T00:00:00Z")))

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getDate("updatedAt")).isEqualTo("2020-10-01T00:00:00Z")
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(MongoOrderRepository.COLLECTION_NAME).find().toList().single()
    }
}