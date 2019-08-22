package com.boclips.event.service.domain

import com.boclips.eventbus.events.user.UserCreated

interface UserRepository {
    fun saveUser(event: UserCreated)
}