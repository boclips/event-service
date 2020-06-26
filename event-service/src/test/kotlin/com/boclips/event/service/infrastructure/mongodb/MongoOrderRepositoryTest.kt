package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.OrderFactory.createOrder
import com.boclips.event.service.testsupport.OrderUserFactory
import com.boclips.event.service.testsupport.OrderUserFactory.createOrderUser
import com.boclips.eventbus.domain.video.VideoId
import com.boclips.eventbus.events.order.OrderItem
import com.boclips.eventbus.events.order.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

class MongoOrderRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var orderRepository: MongoOrderRepository

    @Test
    fun `creating a order`() {
        orderRepository.saveOrder(
            createOrder(
                id = "123",
                status = OrderStatus.COMPLETED,
                createdAt = ZonedDateTime.parse("2019-10-01T00:00:00Z"),
                updatedAt = ZonedDateTime.parse("2020-11-01T00:00:00Z"),
                customerOrganisationName = "pearson",
                items = listOf(
                    OrderItem.builder().priceGbp(BigDecimal("10.50")).videoId(VideoId("the-video-id")).build()
                ),
                    authorisingUser = createOrderUser(email = "louis@hop.com"),
                    requestingUser = createOrderUser(email = "requester@hop.com"),
                    fxRateToGbp = BigDecimal.TEN,
                    currency = Currency.getInstance("USD"),
                    isThroughPlatform = true,
                    isbnOrProductNumber = "jaba-2"
            )
        )

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("123")
        assertThat(document.getString("status")).isEqualTo("COMPLETED")
        assertThat(document.getDate("createdAt")).isEqualTo("2019-10-01T00:00:00Z")
        assertThat(document.getDate("updatedAt")).isEqualTo("2020-11-01T00:00:00Z")
        assertThat(document.getString("customerOrganisationName")).isEqualTo("pearson")
        assertThat(
            document.get("items", List::class.java).first()
        ).isEqualTo(Document(mapOf("videoId" to "the-video-id", "priceGbp" to "10.50")))
        assertThat(document.getString("isbnOrProductNumber")).isEqualTo("jaba-2")
    }

    @Test
    fun `status is UNKNOWN when null in the event`() {
        orderRepository.saveOrder(createOrder(status = null))

        val document = document()
        assertThat(document.getString("status")).isEqualTo("UNKNOWN")
    }

    @Test
    fun `updating a order`() {
        orderRepository.saveOrder(createOrder(id = "1234", updatedAt = ZonedDateTime.parse("2019-10-01T00:00:00Z")))
        orderRepository.saveOrder(createOrder(id = "1234", updatedAt = ZonedDateTime.parse("2020-10-01T00:00:00Z")))

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getDate("updatedAt")).isEqualTo("2020-10-01T00:00:00Z")
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(MongoOrderRepository.COLLECTION_NAME)
            .find().toList().single()
    }
}
