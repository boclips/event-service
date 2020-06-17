package com.boclips.event.aggregator.presentation.formatters

import java.util.UUID

import com.boclips.event.aggregator.domain.model.{SCHOOL_ORGANISATION, User, UserWithRelatedData}
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object SimpleUserFormatter extends SingleRowFormatter[User] {
  override def writeRow(user: User, json: JsonObject): Unit = {
    json.addProperty("id", user.id.value)
    json.addProperty("firstName", user.firstName)
    json.addProperty("lastName", user.lastName)
    json.addProperty("email", user.email)
    json.addProperty("role", user.role)
    json.addProperty("organisationName", user.organisation.map(_.name))
    json.addProperty("organisationType", user.organisation.map(_.`type`).getOrElse(SCHOOL_ORGANISATION).name)
    json.addStringArrayProperty("organisationTags", user.organisation.map(_.tags).getOrElse(Set()))
    json.addProperty("parentOrganisationName", user.organisation.flatMap(_.parent).map(_.name))
    json.addStringArrayProperty("parentOrganisationTags", user.organisation.flatMap(_.parent).map(_.tags).getOrElse(Set()))
    json.addProperty("organisationPostcode", user.organisation.flatMap(_.postcode))
    json.addProperty("organisationState", user.organisation.flatMap(_.state))
    json.addProperty("organisationCountryCode", user.organisation.flatMap(_.countryCode))
    json.addProperty("organisationDealBilling", user.organisation.exists(_.deal.billing))
    json.addProperty("parentOrganisationDealBilling", user.organisation.flatMap(_.parent).exists(_.deal.billing))
    json.addStringArrayProperty("subjects", user.subjects)
    json.addIntArrayProperty("ages", user.ages)
    json.addProperty("isBoclipsEmployee", user.isBoclipsEmployee)
    json.addDateProperty("creationDate", user.createdAt)
    json.addProperty("hasOptedIntoMarketing", user.hasOptedIntoMarketing.getOrElse(false))
  }
}


object UserFormatter extends SingleRowFormatter[UserWithRelatedData] {

  override def writeRow(userWithRelatedData: UserWithRelatedData, json: JsonObject): Unit = {
    val UserWithRelatedData(user, monthlyActiveStatuses, playbacks, referredPlaybacks, searches, sessions) = userWithRelatedData

    SimpleUserFormatter.writeRow(user, json)

    val playbacksJson = playbacks.map(playback => SimplePlaybackFormatter.formatRow(playback))
    json.addJsonArrayProperty("playbacks", playbacksJson)

    val referredPlaybacksJson = referredPlaybacks.map(playback => SimplePlaybackFormatter.formatRow(playback))
    json.addJsonArrayProperty("referredPlaybacks", referredPlaybacksJson)

    val searchesJson = searches.map(search => SearchFormatter.formatRow(search))
    json.addJsonArrayProperty("searches", searchesJson)

    val sessionsJson = sessions.map(session => SessionFormatter.formatRow(session))
    json.addJsonArrayProperty("sessions", sessionsJson)

    val monthlyStatusesJson = monthlyActiveStatuses.map(status => {
      val statusJson = new JsonObject
      statusJson.addMonthProperty("month", status.month)
      statusJson.addProperty("isActive", status.isActive)
      statusJson.addProperty("id", UUID.randomUUID.toString)
      statusJson
    })
    json.addJsonArrayProperty("monthlyStatuses", monthlyStatusesJson)
  }

}
