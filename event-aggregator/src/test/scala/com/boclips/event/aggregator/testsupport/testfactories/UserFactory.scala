package com.boclips.event.aggregator.testsupport.testfactories

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model._
import org.bson.Document

import scala.collection.JavaConverters._

object UserFactory {

  def createOrganisationDocument(
                                  id: String = "org-1",
                                  name: String = "The school of life",
                                  postcode: Option[String] = None,
                                  state: Option[String] = None,
                                  countryCode: Option[String] = None,
                                  parent: Option[Document] = None,
                                  tags: Option[Set[String]] = None,
                                  billing: Option[Boolean] = None,
                                  typeName: String = "SCHOOL",
                                ): Document = {
    new Document(
      Map[String, Object](
        ("id", id),
        ("name", name),
        ("postcode", postcode.orNull),
        ("state", state.orNull),
        ("countryCode", countryCode.orNull),
        ("tags", tags.map(_.toList.asJava).orNull),
        ("parent", parent.orNull),
        ("billing", billing.map(Boolean.box).orNull),
        ("type", typeName)
      ).asJava
    )
  }

  def createUserDocument(
                          id: String = "u123",
                          firstName: Option[String] = None,
                          lastName: Option[String] = None,
                          email: Option[String] = None,
                          role: Option[String] = None,
                          subjects: Option[List[String]] = Some(List()),
                          ages: Option[List[Int]] = Some(List()),
                          createdAt: String = "2019-08-22T14:51:06.368Z",
                          organisation: Option[Document] = None,
                          isBoclipsEmployee: java.lang.Boolean = null,
                          boclipsEmployee: java.lang.Boolean = false
                        ): Document = {

    new Document(
      (
        Map[String, Object](
          ("_id", id),
          ("firstName", firstName.orNull),
          ("lastName", lastName.orNull),
          ("email", email.orNull),
          ("role", role.orNull),
          ("createdAt", createdAt),
          ("organisation", organisation.orNull)
        )
          ++ Option(isBoclipsEmployee).map(b => ("isBoclipsEmployee", b))
          ++ Option(boclipsEmployee).map(b => ("boclipsEmployee", b))
          ++ subjects.map(s => ("subjects", s.asJava))
          ++ ages.map(a => ("ages", a.asJava))
        ).asJava)
  }

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
