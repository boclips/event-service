package com.boclips.event.service.application

import com.boclips.event.service.domain.UserRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated

class UpdateUser(private val userRepository: UserRepository) {

    @BoclipsEventListener
    fun userCreated(userCreated: UserCreated) {
        userRepository.saveUser(userCreated)
    }

    @BoclipsEventListener
    fun userUpdated(userUpdated: UserUpdated) {
        userRepository.updateUser(userUpdated)
    }
}
