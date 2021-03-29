package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.user.UserDocument
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.OrganisationFactory.createAddress
import com.boclips.event.service.testsupport.OrganisationFactory.createDeal
import com.boclips.event.service.testsupport.OrganisationFactory.createOrganisation
import com.boclips.event.service.testsupport.UserFactory.createMarketingTracking
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
                    school = createOrganisation(name = "the school"),
                    hasOptedIntoMarketing = true
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
        assertThat(userDocument().hasOptedIntoMarketing).isEqualTo(true)
    }

    @Test
    fun `saveUser saves organisation`() {
        userRepository.saveUser(
            createUser(
                organisation = createOrganisation(
                    id = "teachers",
                    type = "School",
                    tags = setOf("DESIGN_PARTNER"),
                    features = mapOf("LTI_COPY_RESOURCE_LINK" to false)
                )
            )
        )

        assertThat(userDocument().organisation?.id).isEqualTo("teachers")
        assertThat(userDocument().organisation?.type).isEqualTo("School")
        assertThat(userDocument().organisation?.tags).containsExactly("DESIGN_PARTNER")
        assertThat(userDocument().organisation?.features).isNotNull
        assertThat(userDocument().organisation?.features!!["LTI_COPY_RESOURCE_LINK"]).isFalse()
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

        assertThat(userDocument().boclipsEmployee).isTrue()
    }

    @Test
    fun `saveUser saves address`() {
        val organisation = createOrganisation(
            address = createAddress(
                state = "IL",
                countryCode = "USA",
                postCode = "abc123"
            )
        )
        userRepository.saveUser(createUser(organisation = organisation))

        assertThat(userDocument().organisation!!.state).isEqualTo("IL")
        assertThat(userDocument().organisation!!.countryCode).isEqualTo("USA")
        assertThat(userDocument().organisation!!.postcode).isEqualTo("abc123")
    }

    @Test
    fun `saveUser saves deal`() {
        val organisation = createOrganisation(
            deal = createDeal(
                expiresAt = ZonedDateTime.parse("2020-05-29T00:00:00Z"),
                billing = true
            )
        )
        userRepository.saveUser(createUser(organisation = organisation))

        assertThat(userDocument().organisation!!.dealExpiresAt).isEqualTo("2020-05-29T00:00:00Z")
        assertThat(userDocument().organisation!!.billing).isTrue()
    }

    @Test
    fun `saveUser saves createdAt`() {
        userRepository.saveUser(
            createUser(
                createdAt = ZonedDateTime.of(
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
    fun `saveUser saves marketing tracking details`() {
        userRepository.saveUser(
            createUser(
                profile = createUserProfile(
                    marketingTracking = createMarketingTracking(
                        utmSource = "web",
                        utmTerm = "Fall",
                        utmMedium = "Gas",
                        utmContent = "cat",
                        utmCampaign = "yes we can"
                    )
                )
            )
        )

        assertThat(userDocument().marketingUtmSource).isEqualTo("web")
        assertThat(userDocument().marketingUtmTerm).isEqualTo("Fall")
        assertThat(userDocument().marketingUtmMedium).isEqualTo("Gas")
        assertThat(userDocument().marketingUtmContent).isEqualTo("cat")
        assertThat(userDocument().marketingUtmCampaign).isEqualTo("yes we can")
    }

    @Test
    fun `saves external id`() {
        userRepository.saveUser(createUser(externalUserId = "hello"))

        assertThat(userDocument().externalId).isEqualTo("hello")
    }

    fun userDocument() = document<UserDocument>(MongoUserRepository.COLLECTION_NAME)
}
