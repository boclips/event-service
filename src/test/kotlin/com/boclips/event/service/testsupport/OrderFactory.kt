package com.boclips.event.service.testsupport

import com.boclips.eventbus.events.order.Order
import com.boclips.eventbus.events.order.OrderItem
import com.boclips.eventbus.events.order.OrderStatus
import java.time.ZonedDateTime

object OrderFactory {


    fun createOrder(
        id: String = "order-123",
        status: OrderStatus? = OrderStatus.COMPLETED,
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        updatedAt: ZonedDateTime = ZonedDateTime.now(),
        customerOrganisationName: String = "customer organisation name",
        items: List<OrderItem> = emptyList()
    ): Order {
        return Order.builder()
            .id(id)
            .status(status)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .customerOrganisationName(customerOrganisationName)
            .items(items)
            .build()
    }
}
