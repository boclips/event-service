package com.boclips.event.service.domain

import com.boclips.eventbus.domain.contentpartner.Channel

interface ChannelRepository {
    fun save(channel: Channel)
}
