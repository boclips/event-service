package com.boclips.event.service.application

import com.boclips.event.service.domain.ChannelRepository
import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.domain.contentpartner.ContentPartner
import com.boclips.eventbus.events.contentpartner.BroadcastChannelRequested
import com.boclips.eventbus.events.contentpartner.ContentPartnerUpdated
import com.boclips.eventbus.events.video.VideoBroadcastRequested

class UpdateChannel(private val channelRepository: ChannelRepository) {

    @BoclipsEventListener
    fun channelUpdated(event: ContentPartnerUpdated) {
        save(event.contentPartner)
    }

    @BoclipsEventListener
    fun videoBroadcastRequested(event: BroadcastChannelRequested) {
        save(event.channel)
    }

    private fun save(channel: ContentPartner) {
        channelRepository.save(channel)
    }
}
