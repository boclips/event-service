package com.boclips.event.service.testsupport

import com.boclips.event.service.testsupport.OrderUserFactory.createOrderUser
import com.boclips.eventbus.events.order.Order
import com.boclips.eventbus.events.order.OrderItem
import com.boclips.eventbus.events.order.OrderStatus
import com.boclips.eventbus.events.order.OrderUser
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

object OrderFactory {

    fun createOrder(
            id: String = "order-123",
            status: OrderStatus? = OrderStatus.COMPLETED,
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            updatedAt: ZonedDateTime = ZonedDateTime.now(),
            customerOrganisationName: String = "customer organisation name",
            items: List<OrderItem> = emptyList(),
            authorisingUser: OrderUser? = createOrderUser(email= "doc@mcfly.com"),
            requestingUser: OrderUser = createOrderUser(email= "marty@mcfly.com"),
            isbnOrProductNumber: String? = null,
            isThroughPlatform: Boolean = true,
            currency: Currency? = Currency.getInstance("USD"),
            fxRateToGbp: BigDecimal? = BigDecimal.TEN
    ): Order {
        return Order.builder()
            .id(id)
            .status(status)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .customerOrganisationName(customerOrganisationName)
            .items(items)
                .isbnOrProductNumber(isbnOrProductNumber)
                .isThroughPlatform(isThroughPlatform)
                .currency(currency)
                .fxRateToGbp(fxRateToGbp)
                .authorisingUser(authorisingUser)
                .requestingUser(requestingUser)
            .build()
    }
}
