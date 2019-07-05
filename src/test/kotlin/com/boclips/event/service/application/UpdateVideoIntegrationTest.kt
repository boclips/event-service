package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.DatabaseConstants
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories
import com.boclips.event.service.testsupport.TestFactories.createVideoUpdates
import com.sun.tools.corba.se.idl.som.cff.Messages.msg
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UpdateVideoIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `updates a video`() {
        val event = createVideoUpdates(
            videoId = "video-id",
            title = "the title",
            contentPartnerName = "content partner"
        )

        subscriptions.videoUpdated().send(msg(event))

        assertThat(document().toJson()).contains("the title")
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection("videos").find().toList().single()
    }
}
