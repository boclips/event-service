package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.MongoUserRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
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

        eventBus.publish(UserCreated.builder().user(user).userId("some-id").organisation(organisation).build())

        assertThat(userDocuments()).hasSize(1)
    }

    @Test
    fun `update a user when user is updated`() {
        val user = TestFactories.createUser(
                userId = "some-id",
                isBoclipsEmployee = true
        )
        val organisation = TestFactories.createOrganisation(
                id = "some-org-id"
        )

        eventBus.publish(UserCreated.builder().user(user).userId("some-id").organisation(null).build())
        eventBus.publish(UserUpdated.builder().user(user).userId("some-id").organisation(organisation).build())

        assertThat(userDocuments()).hasSize(1)
        assertThat(userDocuments().first().get("organisation")).isNotNull
    }

    fun userDocuments() = documents(MongoUserRepository.COLLECTION)
}
