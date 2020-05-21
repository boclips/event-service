package com.boclips.event.service.domain

import com.boclips.eventbus.events.order.Order

interface OrderRepository {

    fun saveOrder(order: Order)
}