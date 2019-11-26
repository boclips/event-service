package com.boclips.event.service.application

import com.boclips.event.service.domain.CollectionRepository
import com.boclips.event.service.infrastructure.mongodb.DatabaseConstants
import com.boclips.event.service.infrastructure.mongodb.MongoCollectionRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createCollection
import com.boclips.event.service.testsupport.TestFactories.createVideo
import com.boclips.eventbus.events.collection.CollectionCreated
import com.boclips.eventbus.events.collection.CollectionDeleted
import com.boclips.eventbus.events.collection.CollectionUpdated
import com.boclips.eventbus.events.video.VideoBroadcastRequested
import com.boclips.eventbus.events.video.VideoCreated
import com.boclips.eventbus.events.video.VideoUpdated
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UpdateCollectionIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var collectionRepository: CollectionRepository

    @Test
    fun `collection created`() {
        val collection = createCollection(
            id = "collection-id",
            title = "new collection"
        )

        eventBus.publish(CollectionCreated(collection))

        assertThat(document().toJson()).contains("new collection")
    }

    @Test
    fun `collection updated`() {
        collectionRepository.saveCollection(createCollection(id = "collection-id", title = "collection title"))

        val collection = createCollection(
                id = "collection-id",
                title = "updated collection title"
        )

        eventBus.publish(CollectionUpdated(collection))

        assertThat(document().toJson()).contains("updated collection")
    }

    @Test
    fun `collection deleted`() {
        collectionRepository.saveCollection(createCollection(id = "collection-id", title = "collection title"))

        eventBus.publish(CollectionDeleted.builder()
                .userId("user@example.com")
                .collectionId("collection-id")
                .build())

        assertThat(document().toJson()).contains("\"deleted\": true")
    }

    private fun document(): Document {
        return mongoClient
            .getDatabase(DatabaseConstants.DB_NAME)
            .getCollection(MongoCollectionRepository.COLLECTION_NAME)
            .find()
            .toList()
            .single()
    }
}
