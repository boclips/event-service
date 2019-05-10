package com.boclips.event.service.domain

import com.boclips.events.types.UserActivated

interface EventWriter {
    fun writeUserActivated(userActivated: UserActivated)
}
