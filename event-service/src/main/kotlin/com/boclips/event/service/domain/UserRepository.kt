package com.boclips.event.service.domain

import com.boclips.eventbus.domain.user.User

interface UserRepository {
    fun saveUser(user: User)
}
