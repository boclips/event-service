package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories
import com.boclips.event.service.testsupport.TestFactories.createOrganisation
import com.boclips.event.service.testsupport.TestFactories.createUser
import com.boclips.event.service.testsupport.TestFactories.createUserCreated
import com.boclips.event.service.testsupport.TestFactories.createUserUpdated
import com.boclips.eventbus.domain.user.Organisation
import com.nhaarman.mockito_kotlin.times
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.litote.kmongo.MongoOperator
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZoneOffset
import java.time.ZonedDateTime


class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var userRepository: MongoUserRepository

    @Test
    fun `saveUser saves organisation`() {
        userRepository.saveUser(createUserCreated(organisation = createOrganisation(id = "teachers", type = "School")))

        val organisationDocument = userDocument()["organisation"] as Map<*, *>
        assertThat(organisationDocument["id"]).isEqualTo("teachers")
        assertThat(organisationDocument["type"]).isEqualTo("School")
    }

    @Test
    fun `saveUser handles missing organisation`() {
        userRepository.saveUser(createUserCreated(organisation = null))

        assertThat(userDocument().getString("organisationId")).isNull()
    }

    @Test
    fun `saveUser saves user id`() {
        userRepository.saveUser(createUserCreated(userId = "u1"))

        assertThat(userDocument().getString("_id")).isEqualTo("u1")
    }

    @Test
    fun `saveUser saves isBoclipsEmployee`() {
        userRepository.saveUser(createUserCreated(isBoclipsEmployee = true))

        assertThat(userDocument().getBoolean("boclipsEmployee")).isTrue()
    }

    @Test
    fun `saveUser saves createdAt`() {
        userRepository.saveUser(createUserCreated(timestamp = ZonedDateTime.of(2019, 6, 8, 10, 12, 23, 100, ZoneOffset.UTC)))

        assertThat(userDocument().getString("createdAt")).isEqualTo("2019-06-08T10:12:23Z")
    }

    @Test
    fun `updateUser updates organisation`() {
        userRepository.saveUser(createUserCreated(userId = "u1", organisation = null))
        userRepository.updateUser(createUserUpdated(userId = "u1", organisation = createOrganisation(id = "org1", type = "api")))

        val organisationDocument = userDocument()["organisation"] as Map<*, *>?
        assertThat(organisationDocument).isNotNull
        assertThat(organisationDocument?.get("id")).isEqualTo("org1")
        assertThat(organisationDocument?.get("type")).isEqualTo("api")
    }

    @Test
    fun `updateUser does not update createdAt`() {
        userRepository.saveUser(createUserCreated(userId = "u1", timestamp = ZonedDateTime.of(2019, 6, 8, 10, 12, 23, 100, ZoneOffset.UTC)))
        userRepository.updateUser(createUserUpdated(userId = "u1", timestamp = ZonedDateTime.of(2020, 6, 8, 10, 12, 23, 100, ZoneOffset.UTC)))

        assertThat(userDocument().getString("createdAt")).isEqualTo("2019-06-08T10:12:23Z")
    }

    fun userDocument() = document(MongoUserRepository.COLLECTION)
}
