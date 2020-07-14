package com.boclips.event.aggregator.testsupport.testfactories

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.users
import com.boclips.event.aggregator.domain.model.users._

object UserFactory {

  def createBoclipsUserIdentity(
                                 userId: String = "the-user-id",
                               ): BoclipsUserIdentity = {
    BoclipsUserIdentity(
      UserId(userId),
    )
  }

  def createAnonymousUserIdentity(
                                   deviceId: Option[String] = None,
                                 ): AnonymousUserIdentity = {
    AnonymousUserIdentity(
      deviceId = deviceId.map(DeviceId),
    )
  }

  def createDeal(
                  billing: Boolean = false,
                  dealExpiresAt: Option[ZonedDateTime] = None
                ): Deal = {
    Deal(
      billing = billing,
      dealExpiresAt = dealExpiresAt
    )
  }

  def createOrganisation(
                          name: String = "The Street Wise Academy",
                          parent: Option[Organisation] = None,
                          postcode: Option[String] = None,
                          state: Option[String] = None,
                          countryCode: Option[String] = None,
                          tags: Set[String] = Set(),
                          typeName: OrganisationType = SCHOOL_ORGANISATION,
                          deal: Deal = createDeal(),
                        ): Organisation = {
    users.Organisation(
      name = name,
      postcode = postcode,
      state = state,
      countryCode = countryCode,
      `type` = typeName,
      tags = tags,
      parent = parent,
      deal = deal,
    )
  }

  def createUser(
                  identity: UserIdentity = createBoclipsUserIdentity(),
                  firstName: Option[String] = None,
                  lastName: Option[String] = None,
                  email: Option[String] = None,
                  role: Option[String] = None,
                  subjects: List[String] = List(),
                  ages: List[Int] = List(),
                  isBoclipsEmployee: Boolean = false,
                  organisation: Option[Organisation] = None,
                  profileSchool: Option[Organisation] = None,
                  createdAt: ZonedDateTime = ZonedDateTime.now(),
                  hasOptedIntoMarketing: Option[Boolean] = None,
                  marketingUtmCampaign : Option[String] = None,
                  marketingUtmContent : Option[String] = None,
                  marketingUtmMedium : Option[String] = None,
                  marketingUtmSource : Option[String] = None,
                  marketingUtmTerm : Option[String] = None,
                ): User = {
    User(
      identity = identity,
      firstName = firstName,
      lastName = lastName,
      email = email,
      role = role,
      subjects = subjects,
      ages = ages,
      createdAt = createdAt,
      isBoclipsEmployee = isBoclipsEmployee,
      organisation = organisation,
      profileSchool = profileSchool,
      hasOptedIntoMarketing = hasOptedIntoMarketing,
      marketingUtmCampaign = marketingUtmCampaign,
      marketingUtmContent = marketingUtmContent,
      marketingUtmMedium = marketingUtmMedium,
      marketingUtmSource = marketingUtmSource,
      marketingUtmTerm = marketingUtmTerm,
    )
  }

}
