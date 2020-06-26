package com.boclips.event.service.testsupport

import com.boclips.eventbus.events.order.OrderUser

object OrderUserFactory {

    fun createOrderUser(
           email: String? = "hey@doc.com",
           firstName: String? = null,
           lastName: String? = null,
           legacyUserId: String? = null,
           label: String? = null
    ): OrderUser {
        return OrderUser.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .legacyUserId(legacyUserId)
                .label(label)
                .build()
    }

}