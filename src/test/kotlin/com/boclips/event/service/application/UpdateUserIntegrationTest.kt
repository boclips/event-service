package com.boclips.event.service.application

import com.boclips.event.service.infrastructure.mongodb.MongoUserRepository
import com.boclips.event.service.infrastructure.mongodb.UserDocument
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.OrganisationFactory.createOrganisation
import com.boclips.event.service.testsupport.UserFactory.createUser
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateUserIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `insert a user when user is created`() {
        val organisation = createOrganisation(
            id = "some-org-id"
        )
        val user = createUser(
            id = "some-id",
            isBoclipsEmployee = true,
            organisation = organisation
        )

        eventBus.publish(UserCreated.builder().user(user).build())

        assertThat(userDocuments()).hasSize(1)
    }

    @Test
    fun `update a user when user is updated`() {
        val organisation = createOrganisation(
            id = "some-org-id"
        )
        val userWithUpdatedInformation = createUser(
            id = "some-id",
            isBoclipsEmployee = true,
            organisation = organisation
        )
        val baseUser = createUser(
            id = "some-id",
            isBoclipsEmployee = true,
            organisation = null
        )

        eventBus.publish(UserCreated.builder().user(baseUser).build())
        eventBus.publish(UserUpdated.builder().user(userWithUpdatedInformation).build())

        assertThat(userDocuments()).hasSize(1)
        assertThat(userDocuments().first().organisation).isNotNull
    }

    fun userDocuments() = documents<UserDocument>(MongoUserRepository.COLLECTION_NAME)
}
