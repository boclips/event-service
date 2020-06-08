package com.boclips.event.aggregator.infrastructure.mongo

import java.time.ZonedDateTime
import java.util

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.infrastructure.user.{OrganisationDocument, UserDocument}

import scala.collection.JavaConverters._

object DocumentToUserConverter {

  def convert(document: UserDocument): User = {
    User(
      id = UserId(document.getId),
      firstName = Option(document.getFirstName),
      lastName = Option(document.getLastName),
      email = Option(document.getEmail),
      role = Option(document.getRole),
      subjects = Option(document.getSubjects)
        .map(_.asScala.toList)
        .getOrElse(Nil),
      ages = Option(document.getAges)
        .map(_.asScala.toList.map(_.asInstanceOf[Int]))
        .getOrElse(Nil),
      createdAt = ZonedDateTime.parse(document.getCreatedAt),
      organisation = Option(document.getOrganisation).map(convertOrganisation),
      isBoclipsEmployee = document.getBoclipsEmployee,
      hasOptedIntoMarketing = Option(document.getHasOptedIntoMarketing),
    )
  }

  private def convertOrganisation(document: OrganisationDocument): Organisation = {
    val tags: Set[String] = Option(document.getTags.asInstanceOf[util.List[String]])
      .map(_.asScala.toSet)
      .getOrElse(Set[String]())

    val isBilling: Boolean = if (document.getBilling == null) {
      false
    } else {
      document.getBilling
    }

    Organisation(
      name = document.getName,
      `type` = OrganisationType.from(document.getType),
      parent = Option(document.getParent).map(convertOrganisation),
      postcode = Option(document.getPostcode),
      state = Option(document.getState),
      tags = tags,
      countryCode = Option(document.getCountryCode),
      deal = Deal(isBilling),
    )
  }
}
