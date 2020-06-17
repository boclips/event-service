package com.boclips.event.aggregator.presentation.formatters

import java.time.{Month, YearMonth, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.createPlayback
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearch, createSearchRequest}
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createBoclipsUserIdentity, createDeal, createOrganisation, createUser}
import com.boclips.event.aggregator.testsupport.testfactories.{EventFactory, SessionFactory, UserFactory}

class UserFormatterTest extends Test {

  implicit class UserExtensions(val user: User) {
    def withNested(
                    status: List[UserActiveStatus] = Nil,
                    playbacks: List[Playback] = Nil,
                    referredPlaybacks: List[Playback] = Nil,
                    searches: List[Search] = Nil,
                    sessions: List[Session] = Nil,
                  ): UserWithRelatedData = UserWithRelatedData(user, status, playbacks, referredPlaybacks, searches, sessions)

  }

  implicit def user2userWithRelatedData(user: User): UserWithRelatedData = user.withNested()

  it should "write user id when boclips user" in {
    val json = UserFormatter formatRow createUser(identity = createBoclipsUserIdentity("user-id"))

    json.get("id").getAsString shouldBe "user-id"
    json.get("externalId").getAsString shouldBe ""
  }

  it should "write user id when external user" in {
    val json = UserFormatter formatRow createUser(identity = ExternalUserIdentity(UserId("user1"), ExternalUserId("external1")))

    json.get("id").getAsString shouldBe "user1"
    json.get("externalId").getAsString shouldBe "external1"
  }

  it should "write user personal information" in {
    val json = UserFormatter formatRow createUser(firstName = Some("Jack"), lastName = Some("Jackson"), email = Some("jack@jackson.com"))

    json.get("firstName").getAsString shouldBe "Jack"
    json.get("lastName").getAsString shouldBe "Jackson"
    json.get("email").getAsString shouldBe "jack@jackson.com"
  }

  it should "write organisation name" in {
    val json = UserFormatter formatRow createUser(organisation = Option(createOrganisation(name = "Sesame Street School")))

    json.get("organisationName").getAsString shouldBe "Sesame Street School"
  }

  it should "write organisation type" in {
    val json = UserFormatter formatRow createUser(organisation = Option(createOrganisation(typeName = SCHOOL_ORGANISATION)))

    json.get("organisationType").getAsString shouldBe "SCHOOL"
  }

  it should "write  hasOptedIntoMarketing" in {
    val json = UserFormatter formatRow createUser(hasOptedIntoMarketing = Some(true))

    json.get("hasOptedIntoMarketing").getAsBoolean shouldBe true
  }

  it should "write organisation type as SCHOOL when not known" in {
    val json = UserFormatter formatRow createUser(organisation = None)

    json.getString("organisationType") shouldBe "SCHOOL"
  }

  it should "write parent organisation name" in {
    val json = UserFormatter formatRow createUser(organisation = Option(createOrganisation(parent = Option(createOrganisation(name = "Putnam district")))))

    json.get("parentOrganisationName").getAsString shouldBe "Putnam district"
  }

  it should "write user creation date" in {
    val json = UserFormatter formatRow createUser(createdAt = ZonedDateTime.parse("2017-12-03T10:15:30Z"))

    json.get("creationDate").getAsString shouldBe "2017-12-03"
  }

  it should "write organisation postcode when present" in {
    val json = UserFormatter formatRow createUser(organisation = Option(createOrganisation(postcode = Option("SW4"))))

    json.get("organisationPostcode").getAsString shouldBe "SW4"
  }

  it should "write organisation state and countryCode when present" in {
    val json = UserFormatter formatRow createUser(organisation = Option(createOrganisation(state = Option("LU"), countryCode = Option("GZ"))))

    json.get("organisationState").getAsString shouldBe "LU"
    json.get("organisationCountryCode").getAsString shouldBe "GZ"
  }

  it should "write organisation tags" in {
    val json = UserFormatter formatRow createUser(organisation = Option(createOrganisation(tags = Set("TAG"))))

    json.get("organisationTags").getAsJsonArray.size() shouldBe 1
    json.get("organisationTags").getAsJsonArray.get(0).getAsString shouldBe "TAG"
  }

  it should "write organisation deal billing" in {
    val billedUser = UserFormatter formatRow createUser(organisation = Option(createOrganisation(deal = createDeal(true))))
    val unbilledUser = UserFormatter formatRow createUser(organisation = Option(createOrganisation(deal = createDeal(false))))

    billedUser.get("organisationDealBilling").getAsBoolean shouldBe true
    unbilledUser.get("organisationDealBilling").getAsBoolean shouldBe false
  }

  it should "write organisation deal billing as false when no organisatio" in {
    val user = UserFormatter formatRow createUser(organisation = None)

    user.get("organisationDealBilling").getAsBoolean shouldBe false
  }

  it should "write parent organisation deal billing" in {
    val billedUser = UserFormatter formatRow createUser(organisation = Option(createOrganisation(parent = Option(createOrganisation(deal = createDeal(true))))))
    val unbilledUser = UserFormatter formatRow createUser(organisation = Option(createOrganisation(parent = Option(createOrganisation(deal = createDeal(false))))))

    billedUser.get("parentOrganisationDealBilling").getAsBoolean shouldBe true
    unbilledUser.get("parentOrganisationDealBilling").getAsBoolean shouldBe false
  }

  it should "write parent organisation deal billing as false when no organisation" in {
    val user = UserFormatter formatRow createUser(organisation = None)

    user.get("parentOrganisationDealBilling").getAsBoolean shouldBe false
  }

  it should "write parent organisation deal billing as false when no parent organisation" in {
    val user = UserFormatter formatRow createUser(organisation = Some(createOrganisation(parent = None)))

    user.get("parentOrganisationDealBilling").getAsBoolean shouldBe false
  }

  it should "handle organisation state and countryCode when None" in {
    val json = UserFormatter formatRow createUser(organisation = Option(createOrganisation(state = None, countryCode = None)))

    json.get("organisationState").getAsString shouldBe "UNKNOWN"
    json.get("organisationCountryCode").getAsString shouldBe "UNKNOWN"
  }

  it should "write user role when present" in {
    val json = UserFormatter formatRow createUser(role = Option("paramedic"))

    json.get("role").getAsString shouldBe "paramedic"
  }

  it should "write organisation postcode when absent" in {
    val json = UserFormatter formatRow createUser(organisation = Option(createOrganisation(postcode = None)))

    json.get("organisationPostcode").getAsString shouldBe "UNKNOWN"
  }

  it should "write user subjects" in {
    val json = UserFormatter formatRow createUser(subjects = List("maths", "physics"))

    json.getAsJsonArray("subjects") should have size 2
  }

  it should "write user ages" in {
    val json = UserFormatter formatRow createUser(ages = List(10, 11))

    json.getAsJsonArray("ages") should have size 2
  }

  it should "write isBoclipsEmployee flag" in {
    (UserFormatter formatRow createUser(isBoclipsEmployee = true)).getBool("isBoclipsEmployee") shouldBe true
    (UserFormatter formatRow createUser(isBoclipsEmployee = false)).getBool("isBoclipsEmployee") shouldBe false
  }

  it should "write users monthly active status" in {
    val json = UserFormatter formatRow createUser().withNested(status = List(UserActiveStatus(month = YearMonth.of(2020, Month.APRIL), isActive = true)))

    json.getAsJsonArray("monthlyStatuses") should have size 1
    json.getAsJsonArray("monthlyStatuses").get(0).getAsJsonObject.getString("month") shouldBe "2020-04"
    json.getAsJsonArray("monthlyStatuses").get(0).getAsJsonObject.getBool("isActive") shouldBe true
  }

  it should "write users playbacks" in {
    val json = UserFormatter formatRow createUser().withNested(playbacks = List(createPlayback(id = "pb-1"), createPlayback(id = "pb-2")))
    json.getAsJsonArray("playbacks") should have size 2
  }

  it should "write users referred playbacks" in {
    val json = UserFormatter formatRow createUser().withNested(referredPlaybacks = List(createPlayback(id = "pb-1"), createPlayback(id = "pb-2")))
    json.getAsJsonArray("referredPlaybacks") should have size 2
  }

  it should "write users searches" in {
    val json = UserFormatter formatRow createUser().withNested(searches = List(
      createSearch(request = createSearchRequest(id = "search-1")),
      createSearch(request = createSearchRequest(id = "search-2")),
    ))
    json.getAsJsonArray("searches") should have size 2
  }

  it should "write users sessions" in {
    val json = UserFormatter formatRow createUser().withNested(sessions = List(SessionFactory.createSession(events = List(EventFactory.createCollectionInteractedWithEvent(), EventFactory.createPageRenderedEvent())),
    ))
    json.getAsJsonArray("sessions") should have size 1

  }

}
