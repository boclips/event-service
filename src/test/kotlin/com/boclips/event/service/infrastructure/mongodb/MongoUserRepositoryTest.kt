package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.OrganisationFactory.createOrganisation
import com.boclips.event.service.testsupport.UserFactory.createUser
import com.boclips.event.service.testsupport.UserFactory.createUserProfile
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
        userRepository.saveUser(
            createUser(
                profile = createUserProfile(
                    firstName = "Dave",
                    lastName = "Davidson",
                    school = createOrganisation(name = "the school")
                ),
                email = "dave@example.com",
                organisation = createOrganisation(name = "the organisation")
            )
        )

        assertThat(userDocument().firstName).isEqualTo("Dave")
        assertThat(userDocument().lastName).isEqualTo("Davidson")
        assertThat(userDocument().email).isEqualTo("dave@example.com")
        assertThat(userDocument().organisation?.name).isEqualTo("the organisation")
        assertThat(userDocument().profileSchool?.name).isEqualTo("the school")
    }

    @Test
    fun `saveUser saves organisation`() {
        userRepository.saveUser(
            createUser(
                organisation = createOrganisation(
                    id = "teachers",
                    type = "School",
                    accountType = "Design Partner"
                )
            )
        )

        assertThat(userDocument().organisation?.id).isEqualTo("teachers")
        assertThat(userDocument().organisation?.type).isEqualTo("School")
        assertThat(userDocument().organisation?.accountType).isEqualTo("Design Partner")
    }

    @Test
    fun `saveUser handles missing organisation`() {
        userRepository.saveUser(createUser(organisation = null))

        assertThat(userDocument().organisation).isNull()
    }

    @Test
    fun `saveUser saves user id`() {
        userRepository.saveUser(createUser(id = "u1"))

        assertThat(userDocument().id).isEqualTo("u1")
    }

    @Test
    fun `saveUser saves isBoclipsEmployee`() {
        userRepository.saveUser(createUser(isBoclipsEmployee = true))

        assertThat(userDocument().isBoclipsEmployee).isTrue()
    }

    @Test
    fun `saveUser saves createdAt`() {
        userRepository.saveUser(
            createUser(createdAt = ZonedDateTime.of(
                    2019,
                    6,
                    8,
                    10,
                    12,
                    23,
                    100000000,
                    ZoneOffset.UTC
                )
            )
        )

        assertThat(userDocument().createdAt).isEqualTo("2019-06-08T10:12:23.100Z")
    }

    fun userDocument() = document<UserDocument>(MongoUserRepository.COLLECTION_NAME)
}
