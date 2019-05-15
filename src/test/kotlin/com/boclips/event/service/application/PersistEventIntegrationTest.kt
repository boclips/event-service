package com.boclips.event.service.application

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createCollectionAgeRangeChanged
import com.boclips.event.service.testsupport.TestFactories.createCollectionBookmarked
import com.boclips.event.service.testsupport.TestFactories.createCollectionMadePrivate
import com.boclips.event.service.testsupport.TestFactories.createCollectionMadePublic
import com.boclips.event.service.testsupport.TestFactories.createCollectionSubjectsChanged
import com.boclips.event.service.testsupport.TestFactories.createCollectionUnbookmarked
import com.boclips.event.service.testsupport.TestFactories.createUserActivated
import com.boclips.event.service.testsupport.TestFactories.createVideoAddedToCollection
import com.boclips.event.service.testsupport.TestFactories.createVideoRemovedFromCollection
import com.boclips.event.service.testsupport.TestFactories.createVideoSegmentPlayed
import com.boclips.event.service.testsupport.TestFactories.createVideosSearched
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder

class PersistEventIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun userActivated() {
        val event = createUserActivated(userId = "user-id")

        subscriptions.userActivated().send(msg(event))

        assertThat(document().toJson()).contains("user-id")
    }

    @Test
    fun videosSearched() {
        val event = createVideosSearched(query = "hi")

        subscriptions.videosSearched().send(msg(event))

        assertThat(document().toJson()).contains("hi")
    }

    @Test
    fun videoSegmentPlayed() {
        val event = createVideoSegmentPlayed(videoId = "123")

        subscriptions.videoSegmentPlayed().send(msg(event))

        assertThat(document().toJson()).contains("123")
    }

    @Test
    fun videoAddedToCollection() {
        val event = createVideoAddedToCollection(videoId = "123", collectionId = "456")

        subscriptions.videoAddedToCollection().send(msg(event))

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun videoRemovedFromCollection() {
        val event = createVideoRemovedFromCollection(videoId = "123", collectionId = "456")

        subscriptions.videoRemovedFromCollection().send(msg(event))

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionBookmarked() {
        val event = createCollectionBookmarked(collectionId = "456")

        subscriptions.collectionBookmarked().send(msg(event))

        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionUnbookmarked() {
        val event = createCollectionUnbookmarked(collectionId = "456")

        subscriptions.collectionUnbookmarked().send(msg(event))

        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionMadePrivate() {
        val event = createCollectionMadePrivate(collectionId = "456")

        subscriptions.collectionMadePrivate().send(msg(event))

        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionMadePublic() {
        val event = createCollectionMadePublic(collectionId = "456")

        subscriptions.collectionMadePublic().send(msg(event))

        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionSubjectsChanged() {
        val event = createCollectionSubjectsChanged(collectionId = "456", subjects = setOf("Maths"))

        subscriptions.collectionSubjectsChanged().send(msg(event))

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("Maths")
    }

    @Test
    fun collectionAgeRangeChanged() {
        val event = createCollectionAgeRangeChanged(collectionId = "456", rangeMin = 11, rangeMax = 19)

        subscriptions.collectionAgeRangeChanged().send(msg(event))

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("11")
        assertThat(document().toJson()).contains("19")
    }


    private fun document(): Document {
        return mongoClient.getDatabase("video-service-db").getCollection("event-log").find().toList().single()
    }

    private fun <T> msg(payload: T): Message<T> {
        return MessageBuilder.withPayload(payload).build()
    }
}
