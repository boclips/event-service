package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.ContentPackageRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.ContentPackageFactory.createContentPackage
import org.assertj.core.api.Assertions
import org.bson.Document
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MongoContentPackageRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var contentPackageRepository: ContentPackageRepository

    @Test
    fun `creating a content package`() {
        contentPackageRepository.save(
            createContentPackage(
                id = "content-package-id",
                name = "content package name"
            )
        )

        val document = document()
        Assertions
            .assertThat(document.getString("_id"))
            .isEqualTo("content-package-id")
        Assertions
            .assertThat(document.getString("name"))
            .isEqualTo("content package name")
    }

    @Test
    fun `updating a content package`() {
        contentPackageRepository.save(
            createContentPackage(
                id = "content-package-id",
                name = "content package name"
            )
        )
        contentPackageRepository.save(
            createContentPackage(
                id = "content-package-id",
                name = "new content package name"
            )
        )

        val document = document()
        Assertions
            .assertThat(document.getString("name"))
            .isEqualTo("new content package name")
    }

    private fun document(): Document {
        return mongoClient.getDatabase(DatabaseConstants.DB_NAME)
            .getCollection(MongoContentPackageRepository.COLLECTION_NAME)
            .find()
            .toList()
            .single()
    }
}
