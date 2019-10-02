package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories
import com.boclips.event.service.testsupport.TestFactories.createOrganisation
import com.boclips.event.service.testsupport.TestFactories.createUser
import com.boclips.event.service.testsupport.TestFactories.createUserCreated
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

        assertThat(userDocument().getBoolean("isBoclipsEmployee")).isTrue()
    }

    @Test
    fun `saveUser saves createdAt`() {
        userRepository.saveUser(createUserCreated(timestamp = ZonedDateTime.of(2019, 6, 8, 10, 12, 23, 100, ZoneOffset.UTC)))

        assertThat(userDocument().getString("createdAt")).isEqualTo("2019-06-08T10:12:23Z")
    }

    fun userDocument() = document(MongoUserRepository.COLLECTION)
}
