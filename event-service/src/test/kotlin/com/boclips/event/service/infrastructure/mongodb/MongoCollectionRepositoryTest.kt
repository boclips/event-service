package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.CollectionRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.CollectionFactory.createCollection
import com.boclips.eventbus.domain.AgeRange
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZoneOffset
import java.time.ZonedDateTime

class MongoCollectionRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var collectionRepository: CollectionRepository

    @Test
    fun `creating a collection`() {
        collectionRepository.saveCollection(
            createCollection(
                id = "1234",
                title = "collection title",
                description = "collection description",
                subjects = setOf("maths", "physics"),
                ageRange = AgeRange(10, null),
                videoIds = listOf("v1", "v2"),
                ownerId = "user@example.com",
                createdTime = ZonedDateTime.of(2019, 10, 15, 10, 11, 12, 0, ZoneOffset.UTC),
                updatedTime = ZonedDateTime.of(2019, 11, 15, 10, 11, 12, 0, ZoneOffset.UTC),
                bookmarks = listOf("anotheruser@example.com"),
                isDiscoverable = false,
                isDefault = true
            )
        )

        val document = document()
        assertThat(document.getString("_id")).isEqualTo("1234")
        assertThat(document.getString("title")).isEqualTo("collection title")
        assertThat(document.getString("description")).isEqualTo("collection description")
        assertThat(document.getList("subjects", String::class.java)).containsExactlyInAnyOrder("maths", "physics")
        assertThat(document.getInteger("minAge")).isEqualTo(10)
        assertThat(document.get("maxAge", Integer::class.java)).isEqualTo(null)
        assertThat(document.getList("videoIds", String::class.java)).containsExactly("v1", "v2")
        assertThat(document.getString("ownerId")).isEqualTo("user@example.com")
        assertThat(document.getList("bookmarks", String::class.java)).containsExactly("anotheruser@example.com")
        assertThat(document.getDate("createdTime")).isEqualTo("2019-10-15T10:11:12Z")
        assertThat(document.getDate("updatedTime")).isEqualTo("2019-11-15T10:11:12Z")
        assertThat(document.getBoolean("discoverable")).isEqualTo(false)
        assertThat(document.getBoolean("createdForOwner")).isEqualTo(true)
        assertThat(document.getBoolean("deleted")).isEqualTo(false)
    }

    @Test
    fun `updating a collection`() {
        collectionRepository.saveCollection(createCollection(id = "1", title = "Old title"))
        collectionRepository.saveCollection(createCollection(id = "1", title = "New title"))

        val document = document()
        assertThat(document.getString("title")).isEqualTo("New title")
    }

    @Test
    fun `marking a collection as deleted`() {
        collectionRepository.saveCollection(createCollection(id = "1", title = "The title"))

        collectionRepository.markDeleted("1")

        val document = document()
        assertThat(document.getString("title")).isEqualTo("The title")
        assertThat(document.getBoolean("deleted")).isEqualTo(true)
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME)
            .getCollection(MongoCollectionRepository.COLLECTION_NAME).find().toList().single()
    }
}
