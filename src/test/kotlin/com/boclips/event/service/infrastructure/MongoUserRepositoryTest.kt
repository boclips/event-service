package com.boclips.event.service.infrastructure

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.TestFactories.createOrganisation
import com.boclips.event.service.testsupport.TestFactories.createUserCreated
import com.boclips.event.service.testsupport.TestFactories.createUserUpdated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZoneOffset
import java.time.ZonedDateTime


class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var userRepository: MongoUserRepository

    @Test
    fun `saveUser saves user details`() {
        userRepository.saveUser(createUserCreated(firstName = "Dave", lastName = "Davidson", email = "dave@example.com"))

        assertThat(userDocument().getString("firstName")).isEqualTo("Dave")
        assertThat(userDocument().getString("lastName")).isEqualTo("Davidson")
        assertThat(userDocument().getString("email")).isEqualTo("dave@example.com")
    }

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
    fun `updateUser updates user details`() {
        userRepository.saveUser(createUserCreated(userId = "u1", organisation = null))
        userRepository.updateUser(createUserUpdated(userId = "u1", firstName = "Bob", lastName = "Bobson", email = "bob@email.com"))

        assertThat(userDocument().getString("firstName")).isEqualTo("Bob")
        assertThat(userDocument().getString("lastName")).isEqualTo("Bobson")
        assertThat(userDocument().getString("email")).isEqualTo("bob@email.com")
    }

    @Test
    fun `updateUser updates organisation`() {
        userRepository.saveUser(createUserCreated(userId = "u1", organisation = null))
        userRepository.updateUser(createUserUpdated(userId = "u1", organisation = createOrganisation(id = "org1", type = "api", name = "org name", postcode = "12345", parent = createOrganisation(name = "parent org"))))

        val organisationDocument = userDocument()["organisation"] as Map<*, *>?
        assertThat(organisationDocument).isNotNull
        assertThat(organisationDocument?.get("id")).isEqualTo("org1")
        assertThat(organisationDocument?.get("type")).isEqualTo("api")
        assertThat(organisationDocument?.get("name")).isEqualTo("org name")
        assertThat(organisationDocument?.get("postcode")).isEqualTo("12345")
        assertThat(organisationDocument?.get("parent") as Map<*, *>?).isNotNull
        assertThat((organisationDocument?.get("parent") as Map<*, *>?)?.get("name")).isEqualTo("parent org")
    }

    @Test
    fun `updateUser does not update createdAt`() {
        userRepository.saveUser(createUserCreated(userId = "u1", timestamp = ZonedDateTime.of(2019, 6, 8, 10, 12, 23, 100, ZoneOffset.UTC)))
        userRepository.updateUser(createUserUpdated(userId = "u1", timestamp = ZonedDateTime.of(2020, 6, 8, 10, 12, 23, 100, ZoneOffset.UTC)))

        assertThat(userDocument().getString("createdAt")).isEqualTo("2019-06-08T10:12:23Z")
    }

    fun userDocument() = document(MongoUserRepository.COLLECTION)
}
