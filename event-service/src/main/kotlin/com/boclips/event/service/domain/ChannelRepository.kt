package com.boclips.event.service.domain

import com.boclips.eventbus.domain.contentpartner.ContentPartner

interface ChannelRepository {
    fun save(channel: ContentPartner)
}