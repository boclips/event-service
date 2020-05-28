package com.boclips.event.aggregator.infrastructure.mongo

import java.time.ZonedDateTime
import java.util

import com.boclips.event.aggregator.domain.model._
import org.bson.Document

import scala.collection.JavaConverters._

object DocumentToUserConverter {

  def convert(document: Document): User = {

    val organisation = convertOrganisation(Option(document.get("organisation", classOf[util.Map[String, Object]])))

    User(
      id = UserId(document.getString("_id")),
      firstName = Option(document.getString("firstName")),
      lastName = Option(document.getString("lastName")),
      email = Option(document.getString("email")),
      role = Option(document.getString("role")),
      subjects = document.getList[String]("subjects"),
      ages = document.getList[Int]("ages"),
      createdAt = ZonedDateTime.parse(document.getString("createdAt")),
      organisation = organisation,
      isBoclipsEmployee = Option(document.getBoolean("isBoclipsEmployee")).getOrElse(document.getBoolean("boclipsEmployee")).booleanValue()
    )
  }

  private def convertOrganisation(organisationDocument: Option[util.Map[String, Object]]): Option[Organisation] = organisationDocument.map(document => {

    val tags: Set[String] = Option(document.get("tags").asInstanceOf[util.List[String]])
      .map(_.asScala.toSet)
      .getOrElse(Set[String]())

    Organisation(
      name = document.get("name").toString,
      `type` = OrganisationType.from(document.get("type").toString),
      parent = convertOrganisation(Option(document.get("parent").asInstanceOf[util.Map[String, Object]])),
      postcode = Option(document.get("postcode")).map(_.toString),
      state = Option(document.get("state")).map(_.toString),
      tags = tags,
      countryCode = Option(document.get("countryCode")).map(_.toString),
      deal = Deal(
        billing = document.get("billing").asInstanceOf[Boolean],
      ),
    )
  }
  )
}
