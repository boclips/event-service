package com.boclips.event.service.application

import com.boclips.event.infrastructure.contract.ContractDocument
import com.boclips.event.service.domain.ContentPackageRepository
import com.boclips.event.service.infrastructure.mongodb.MongoContentPackageRepository
import com.boclips.event.service.infrastructure.mongodb.MongoContractRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.ContentPackageFactory.createContentPackage
import com.boclips.event.service.testsupport.ContractFactory
import com.boclips.eventbus.events.contentpackage.ContentPackageBroadcastRequested
import com.boclips.eventbus.events.contentpackage.ContentPackageUpdated
import com.boclips.eventbus.events.contract.BroadcastContractRequested
import com.boclips.eventbus.events.contract.ContractUpdated
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class UpdateContentPackageIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `insert a content package when update event is received`() {
        val contentPackage = createContentPackage(
            id = "content-package-id",
            name = "content package title"
        )

        eventBus.publish(ContentPackageUpdated(contentPackage))

        val document = getSingleDocument()
        Assertions.assertThat(document.id).isEqualTo("content-package-id")
        Assertions.assertThat(document.name).isEqualTo("content package title")
    }

    @Test
    fun `insert a content package when broadcast event is received`() {
        val contentPackage = createContentPackage(
            id = "content-package-id",
            name = "content package title"
        )

        eventBus.publish(ContentPackageBroadcastRequested(contentPackage))

        val document = getSingleDocument()
        Assertions.assertThat(document.id).isEqualTo("content-package-id")
        Assertions.assertThat(document.name).isEqualTo("content package title")
    }

    fun getSingleDocument() = document<ContractDocument>(
        MongoContentPackageRepository.COLLECTION_NAME
    )
}
