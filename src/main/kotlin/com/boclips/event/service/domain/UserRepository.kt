package com.boclips.event.service.domain

import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated

interface UserRepository {
    fun saveUser(event: UserCreated)

    fun updateUser(event: UserUpdated)
}
