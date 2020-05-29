package com.boclips.event.aggregator.testsupport.testfactories

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import org.bson.Document

import scala.collection.JavaConverters._

object UserFactory {
  def createDeal(
                  billing: Boolean = false
                ): Deal = {
    Deal(
      billing = billing
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
    Organisation(
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
                  id: String = "userId",
                  firstName: Option[String] = None,
                  lastName: Option[String] = None,
                  email: Option[String] = None,
                  role: Option[String] = None,
                  subjects: List[String] = List(),
                  ages: List[Int] = List(),
                  isBoclipsEmployee: Boolean = false,
                  organisation: Option[Organisation] = None,
                  createdAt: ZonedDateTime = ZonedDateTime.now()
                ): User = {
    User(
      id = UserId(id),
      firstName = firstName,
      lastName = lastName,
      email = email,
      role = role,
      subjects = subjects,
      ages = ages,
      createdAt = createdAt,
      isBoclipsEmployee = isBoclipsEmployee,
      organisation = organisation
    )
  }

  def createAnonymousUser(
                           deviceId: DeviceId = DeviceId("device-id"),
                         ): AnonymousUser = {
    AnonymousUser(
      deviceId = deviceId,
    )
  }
}
