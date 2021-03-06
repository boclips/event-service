package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.mongodb.DatabaseConstants
import com.boclips.event.service.infrastructure.mongodb.MongoVideoRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.VideoFactory.createVideo
import com.boclips.eventbus.events.video.VideoBroadcastRequested
import com.boclips.eventbus.events.video.VideoCreated
import com.boclips.eventbus.events.video.VideoUpdated
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test

class UpdateVideoIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `insert a video when created`() {
        val video = createVideo(
            id = "video-id",
            title = "new video",
            channelId = "channel id"
        )

        eventBus.publish(VideoCreated(video))

        assertThat(document().toJson()).contains("new video")
    }

    @Test
    fun `update a video on update`() {
        val video = createVideo(
            id = "video-id",
            title = "the title",
            channelId = "channel id"
        )

        eventBus.publish(VideoUpdated(video))

        assertThat(document().toJson()).contains("the title")
    }

    @Test
    fun `update a video on broadcast`() {
        val video = createVideo(
            id = "video-id",
            title = "the title",
            channelId = "channel id"
        )

        eventBus.publish(VideoBroadcastRequested(video))

        assertThat(document().toJson()).contains("the title")
    }

    private fun document(): Document {
        return mongoClient
            .getDatabase(DatabaseConstants.DB_NAME)
            .getCollection(MongoVideoRepository.COLLECTION_NAME)
            .find()
            .toList()
            .single()
    }
}
