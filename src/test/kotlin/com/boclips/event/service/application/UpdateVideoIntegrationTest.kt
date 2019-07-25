package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.DatabaseConstants
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createVideo
import com.boclips.event.service.testsupport.TestFactories.createVideoCreated
import com.boclips.event.service.testsupport.TestFactories.createVideoUpdated
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test

class UpdateVideoIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `insert a video when created`() {
        val video = createVideo(
            videoId = "video-id",
            title = "new video",
            contentPartnerName = "content partner"
        )

        eventBus
            .publish(createVideoCreated(video))

        assertThat(document().toJson())
            .contains("new video")
    }

    @Test
    fun `update a video on update`() {
        val video = createVideo(
            videoId = "video-id",
            title = "the title",
            contentPartnerName = "content partner"
        )

        eventBus
            .publish(createVideoUpdated(video))

        assertThat(document().toJson())
            .contains("the title")
    }

    private fun document(): Document {
        return mongoClient
            .getDatabase(DatabaseConstants.DB_NAME)
            .getCollection("videos")
            .find()
            .toList()
            .single()
    }
}
