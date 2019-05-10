package com.boclips.event.service.testsupport

import com.boclips.events.types.User
import com.boclips.events.types.UserActivated

object TestFactories {

    fun createUserActivated(userId: String = "user-1", userEmail: String = "user@example.com"): UserActivated {
        return UserActivated.builder()
                .user(User.builder().id(userId).email(userEmail).build())
                .totalUsers(100)
                .activatedUsers(50)
                .build()
    }
}
