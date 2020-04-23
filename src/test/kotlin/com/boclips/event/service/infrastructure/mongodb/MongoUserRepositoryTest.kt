package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.EventFactory.createUserCreated
import com.boclips.event.service.testsupport.EventFactory.createUserUpdated
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
            createUserCreated(
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
            createUserCreated(
                createUser(
                    organisation = createOrganisation(
                        id = "teachers",
                        type = "School",
                        accountType = "Design Partner"
                    )
                )
            )
        )

        assertThat(userDocument().organisation?.id).isEqualTo("teachers")
        assertThat(userDocument().organisation?.type).isEqualTo("School")
        assertThat(userDocument().organisation?.accountType).isEqualTo("Design Partner")
    }

    @Test
    fun `saveUser handles missing organisation`() {
        userRepository.saveUser(createUserCreated(createUser(organisation = null)))

        assertThat(userDocument().organisation).isNull()
    }

    @Test
    fun `saveUser saves user id`() {
        userRepository.saveUser(createUserCreated(createUser(id = "u1")))

        assertThat(userDocument().id).isEqualTo("u1")
    }

    @Test
    fun `saveUser saves isBoclipsEmployee`() {
        userRepository.saveUser(createUserCreated(createUser(isBoclipsEmployee = true)))

        assertThat(userDocument().isBoclipsEmployee).isTrue()
    }

    @Test
    fun `saveUser saves createdAt`() {
        userRepository.saveUser(
            createUserCreated(
                timestamp = ZonedDateTime.of(
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

    @Test
    fun `updateUser updates user profile`() {
        userRepository.saveUser(createUserCreated(createUser(id = "u1")))

        userRepository.updateUser(
            createUserUpdated(
                createUser(
                    id = "u1",
                    profile = createUserProfile(
                        firstName = "Bob",
                        lastName = "Bobson",
                        role = "new role",
                        school = createOrganisation(name = "new school")
                    ),
                    email = "bob@email.com"
                )
            )
        )

        assertThat(userDocument().firstName).isEqualTo("Bob")
        assertThat(userDocument().lastName).isEqualTo("Bobson")
        assertThat(userDocument().email).isEqualTo("bob@email.com")
        assertThat(userDocument().role).isEqualTo("new role")
        assertThat(userDocument().profileSchool?.name).isEqualTo("new school")
    }

    @Test
    fun `updateUser updates organisation`() {
        userRepository.saveUser(createUserCreated(user = createUser(id = "u1", organisation = null)))
        userRepository.updateUser(
            createUserUpdated(
                user = createUser(
                    id = "u1", organisation = createOrganisation(
                        id = "org1",
                        accountType = "DESIGN_PARTNER",
                        type = "api",
                        name = "org name",
                        postcode = "12345",
                        parent = createOrganisation(
                            name = "parent org",
                            accountType = "DESIGN_PARTNER"
                        )
                    )
                )
            )
        )

        assertThat(userDocument().organisation).isNotNull
        assertThat(userDocument().organisation?.id).isEqualTo("org1")
        assertThat(userDocument().organisation?.accountType).isEqualTo("DESIGN_PARTNER")
        assertThat(userDocument().organisation?.type).isEqualTo("api")
        assertThat(userDocument().organisation?.name).isEqualTo("org name")
        assertThat(userDocument().organisation?.postcode).isEqualTo("12345")
        assertThat(userDocument().organisation?.parent).isNotNull
        assertThat(userDocument().organisation?.parent?.name).isEqualTo("parent org")
        assertThat(userDocument().organisation?.parent?.accountType).isEqualTo("DESIGN_PARTNER")
    }

    @Test
    fun `updateUser updates user subjects`() {
        userRepository.saveUser(createUserCreated(createUser(id = "u1")))
        userRepository.updateUser(
            createUserUpdated(
                createUser(
                    id = "u1",
                    profile = createUserProfile(subjectNames = listOf("Maths"))
                )
            )
        )

        assertThat(userDocument().subjects).containsExactly("Maths")
    }

    @Test
    fun `updateUser updates user ages`() {
        userRepository.saveUser(createUserCreated(createUser(id = "u1")))
        userRepository.updateUser(
            createUserUpdated(
                createUser(
                    id = "u1",
                    profile = createUserProfile(ages = listOf(10))
                )
            )
        )

        assertThat(userDocument().ages).isEqualTo(listOf(10))
    }

    @Test
    fun `updateUser does not update createdAt`() {
        userRepository.saveUser(
            createUserCreated(
                user = createUser(id = "u1"),
                timestamp = ZonedDateTime.of(2019, 6, 8, 10, 12, 23, 100000000, ZoneOffset.UTC)
            )
        )
        userRepository.updateUser(
            createUserUpdated(
                user = createUser(id = "u1"),
                timestamp = ZonedDateTime.of(2020, 6, 8, 10, 12, 23, 200000000, ZoneOffset.UTC)
            )
        )

        assertThat(userDocument().createdAt).isEqualTo("2019-06-08T10:12:23.100Z")
    }

    fun userDocument() = document<UserDocument>(MongoUserRepository.COLLECTION_NAME)
}
