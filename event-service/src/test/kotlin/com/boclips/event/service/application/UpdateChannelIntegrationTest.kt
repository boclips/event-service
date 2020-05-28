package com.boclips.event.service.application

import com.boclips.event.infrastructure.channel.ChannelDocument
import com.boclips.event.service.infrastructure.mongodb.MongoChannelRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.ChannelFactory.createChannel
import com.boclips.eventbus.events.contentpartner.BroadcastChannelRequested
import com.boclips.eventbus.events.contentpartner.ContentPartnerUpdated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateChannelIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `insert a channel when update event is received`() {
        val channel = createChannel(
            id = "channel-id",
            name = "channel title"
        )

        eventBus.publish(ContentPartnerUpdated(channel))

        val document = getSingleDocument()
        assertThat(document._id).isEqualTo(channel.id.value)
        assertThat(document.name).isEqualTo(channel.name)
    }

    @Test
    fun `insert a channel when broadcast event is received`() {
        val channel = createChannel(
            id = "channel-id",
            name = "channel title"
        )

        eventBus.publish(BroadcastChannelRequested(channel))

        val document = getSingleDocument()
        assertThat(document._id).isEqualTo(channel.id.value)
        assertThat(document.name).isEqualTo(channel.name)
    }

    fun getSingleDocument() = document<ChannelDocument>(MongoChannelRepository.COLLECTION_NAME)
}