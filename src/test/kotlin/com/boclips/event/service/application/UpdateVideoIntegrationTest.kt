package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.DatabaseConstants
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createVideoUpdates
import com.boclips.events.config.subscriptions.VideoUpdatedSubscription
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UpdateVideoIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var videoUpdated: VideoUpdatedSubscription

    @Test
    fun `updates a video`() {
        val event = createVideoUpdates(
            videoId = "video-id",
            title = "the title",
            contentPartnerName = "content partner"
        )

        videoUpdated.channel().send(msg(event))

        assertThat(document().toJson()).contains("the title")
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("videos").find().toList().single()
    }
}
