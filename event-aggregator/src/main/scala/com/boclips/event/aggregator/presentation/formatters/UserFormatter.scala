package com.boclips.event.aggregator.presentation.formatters

import java.util.UUID

import com.boclips.event.aggregator.domain.model.users._
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.boclips.event.aggregator.presentation.model.UserTableRow
import com.google.gson.JsonObject

object SimpleUserFormatter extends SingleRowFormatter[User] {
  override def writeRow(user: User, json: JsonObject): Unit = {
    val (id, identity) = user.identity match {
      case BoclipsUserIdentity(id) => (id.value, "BOCLIPS")
      case ExternalUserIdentity(id, externalId) => (s"${id.value}/${externalId.value}", "EXTERNAL")
      case AnonymousUserIdentity(Some(deviceId)) => (s"device:${deviceId.value}", "ANONYMOUS")
      case AnonymousUserIdentity(None) => throw new IllegalArgumentException()
    }

    json.addProperty("id", id)
    json.addProperty("identity", identity)
    json.addProperty("externalId", user.externalUserId)
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

    json.addProperty("profileOrganisationName", user.profileSchool.map(_.name))
    json.addProperty("profileOrganisationType", user.profileSchool.map(_.`type`).getOrElse(SCHOOL_ORGANISATION).name)
    json.addStringArrayProperty("profileOrganisationTags", user.profileSchool.map(_.tags).getOrElse(Set()))
    json.addProperty("profileParentOrganisationName", user.profileSchool.flatMap(_.parent).map(_.name))
    json.addProperty("profileOrganisationPostcode", user.profileSchool.flatMap(_.postcode))
    json.addProperty("profileOrganisationState", user.profileSchool.flatMap(_.state))
    json.addProperty("profileOrganisationCountryCode", user.profileSchool.flatMap(_.countryCode))

    json.addStringArrayProperty("subjects", user.subjects)
    json.addIntArrayProperty("ages", user.ages)
    json.addProperty("isBoclipsEmployee", user.isBoclipsEmployee)
    json.addDateProperty("creationDate", user.createdAt)
    json.addProperty("hasOptedIntoMarketing", user.hasOptedIntoMarketing.getOrElse(false))
    json.addProperty("marketingUtmCampaign", user.marketingUtmCampaign)
    json.addProperty("marketingUtmContent", user.marketingUtmContent)
    json.addProperty("marketingUtmMedium", user.marketingUtmMedium)
    json.addProperty("marketingUtmSource", user.marketingUtmSource)
    json.addProperty("marketingUtmTerm", user.marketingUtmTerm)
  }
}


object UserFormatter extends SingleRowFormatter[UserTableRow] {

  override def writeRow(userWithRelatedData: UserTableRow, json: JsonObject): Unit = {
    val UserTableRow(user, monthlyActiveStatuses, playbacks, referredPlaybacks, searches, sessions, interactions) = userWithRelatedData

    SimpleUserFormatter.writeRow(user, json)

    val playbacksJson = playbacks.map(playback => SimplePlaybackFormatter.formatRow(playback))
    json.addJsonArrayProperty("playbacks", playbacksJson)

    val referredPlaybacksJson = referredPlaybacks.map(playback => SimplePlaybackFormatter.formatRow(playback))
    json.addJsonArrayProperty("referredPlaybacks", referredPlaybacksJson)

    val searchesJson = searches.map(search => SearchFormatter.formatRow(search))
    json.addJsonArrayProperty("searches", searchesJson)

    val sessionsJson = sessions.map(session => SessionFormatter.formatRow(session))
    json.addJsonArrayProperty("sessions", sessionsJson)

    val interactionsJson = interactions.map(interaction => {
    val interactionJson = new JsonObject
      interactionJson.addDateTimeProperty("timestamp", interaction.timestamp)
      interactionJson.addProperty("videoId", interaction.videoId.value)
      interactionJson.addProperty("subtype", interaction.subtype)
      interactionJson.addProperty("urlHost", interaction.url.map(_.host))
      interactionJson
    }
    )
    json.addJsonArrayProperty("interactions", interactionsJson)

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
