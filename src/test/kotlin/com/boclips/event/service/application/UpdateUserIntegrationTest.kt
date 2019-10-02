package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.MongoUserRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories
import com.boclips.eventbus.events.user.UserCreated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateUserIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `insert a user when user is created`() {
        val user = TestFactories.createUser(
                userId = "some-id",
                isBoclipsEmployee = true
        )
        val organisation = TestFactories.createOrganisation(
                id = "some-org-id"
        )

        eventBus.publish(UserCreated.builder().user(user).organisation(organisation).build())

        assertThat(userDocuments()).hasSize(1)
    }

    fun userDocuments() = documents(MongoUserRepository.COLLECTION)
}
