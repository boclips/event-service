package com.boclips.event.service.application

import com.boclips.event.service.domain.OrderRepository
import com.boclips.event.service.infrastructure.mongodb.MongoOrderRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.OrderFactory.createOrder
import com.boclips.eventbus.events.order.OrderCreated
import com.boclips.eventbus.events.order.OrderUpdated
import com.order.OrderDocument
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UpdateOrderIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Test
    fun `insert an order when one is created`() {
        val order = createOrder(id = "order-id")

        eventBus.publish(OrderCreated.builder().order(order).build())

        assertThat(orderDocuments()).hasSize(1)
    }

    @Test
    fun `update an order when one is updated`() {
        val order = createOrder(id = "order-id", customerOrganisationName = "prev name")
        orderRepository.saveOrder(order)
        val updatedOrder = createOrder(id = "order-id", customerOrganisationName = "new name")

        eventBus.publish(OrderUpdated.builder().order(updatedOrder).build())

        assertThat(orderDocuments()).hasSize(1)
        assertThat(orderDocuments().single().customerOrganisationName).isEqualTo("new name")
    }

    private fun orderDocuments() = documents<OrderDocument>(MongoOrderRepository.COLLECTION_NAME)
}
