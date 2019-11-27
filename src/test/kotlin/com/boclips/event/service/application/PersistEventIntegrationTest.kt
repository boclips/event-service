package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.mongodb.DatabaseConstants
import com.boclips.event.service.infrastructure.mongodb.MongoEventWriter
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createCollectionAgeRangeChanged
import com.boclips.event.service.testsupport.TestFactories.createCollectionBookmarkChanged
import com.boclips.event.service.testsupport.TestFactories.createCollectionInteractedWith
import com.boclips.event.service.testsupport.TestFactories.createCollectionSubjectsChanged
import com.boclips.event.service.testsupport.TestFactories.createCollectionVisibilityChanged
import com.boclips.event.service.testsupport.TestFactories.createPageRendered
import com.boclips.event.service.testsupport.TestFactories.createUser
import com.boclips.event.service.testsupport.TestFactories.createVideoAddedToCollection
import com.boclips.event.service.testsupport.TestFactories.createVideoInteractedWith
import com.boclips.event.service.testsupport.TestFactories.createVideoPlayerInteractedWith
import com.boclips.event.service.testsupport.TestFactories.createVideoRemovedFromCollection
import com.boclips.event.service.testsupport.TestFactories.createVideoSegmentPlayed
import com.boclips.event.service.testsupport.TestFactories.createVideosSearched
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test

class PersistEventIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun videosSearched() {
        val event = createVideosSearched(query = "hi")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("hi")
    }

    @Test
    fun videoSegmentPlayed() {
        val event = createVideoSegmentPlayed(videoId = "123")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("123")
    }

    @Test
    fun videoPlayerInteractedWith() {
        val event = createVideoPlayerInteractedWith(videoId = "123")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("123")
    }

    @Test
    fun videoAddedToCollection() {
        val event = createVideoAddedToCollection(videoId = "123", collectionId = "456")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun videoRemovedFromCollection() {
        val event = createVideoRemovedFromCollection(videoId = "123", collectionId = "456")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("123")
        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionBookmarkChanged() {
        val event = createCollectionBookmarkChanged(collectionId = "456", isBookmarked = true, user = createUser())

        eventBus.publish(event)

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("true")
    }

    @Test
    fun collectionVisibilityChanged() {
        val event = createCollectionVisibilityChanged(collectionId = "456", isPublic = false)

        eventBus.publish(event)

        assertThat(document().toJson()).contains("456")
    }

    @Test
    fun collectionSubjectsChanged() {
        val event = createCollectionSubjectsChanged(collectionId = "456", subjects = setOf("Maths"))

        eventBus.publish(event)

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("Maths")
    }

    @Test
    fun collectionAgeRangeChanged() {
        val event = createCollectionAgeRangeChanged(collectionId = "456", rangeMin = 11, rangeMax = 19)

        eventBus.publish(event)

        assertThat(document().toJson()).contains("456")
        assertThat(document().toJson()).contains("11")
        assertThat(document().toJson()).contains("19")
    }

    @Test
    fun collectionInteractedWith() {
        val event = createCollectionInteractedWith(collectionId = "test-id")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("test-id")
    }

    @Test
    fun videoInteractedWith() {
        val event = createVideoInteractedWith(videoId = "the video id")

        eventBus.publish(event)

        assertThat(document().toJson()).contains("the video id")
    }

    @Test
    fun pageRendered(){
        val event = createPageRendered(url = "http://teachers.boclips.com/my-videos")
        eventBus.publish(event)
        assertThat(document().toJson()).contains("http://teachers.boclips.com/my-videos")
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection(MongoEventWriter.COLLECTION_NAME).find().toList().single()
    }


}
