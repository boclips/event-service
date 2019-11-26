package com.boclips.event.service.application

import com.boclips.event.service.domain.OrderRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.events.order.OrderCreated
import com.boclips.eventbus.events.order.OrderUpdated

class UpdateOrder(private val orderRepository: OrderRepository) {

    @BoclipsEventListener
    fun orderCreated(event: OrderCreated) {
        orderRepository.saveOrder(event.order)
    }

    @BoclipsEventListener
    fun orderUpdated(event: OrderUpdated) {
        orderRepository.saveOrder(event.order)
    }
}