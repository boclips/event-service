package com.boclips.event.aggregator.presentation.formatters

import java.time.LocalDate

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object NestedOrderFormatter extends SingleRowFormatter[VideoItemWithOrder] {
  override def writeRow(obj: VideoItemWithOrder, json: JsonObject): Unit = {
    json.addProperty("id", obj.order.id.value + "_" + obj.item.videoId.value)
    json.addProperty("orderId", obj.order.id.value)
    json.addProperty("priceGbp", obj.item.priceGbp)
    json.addProperty("customerOrganisationName", obj.order.customerOrganisationName)
    json.addDateTimeProperty("orderCreatedAt", obj.order.createdAt)
    json.addDateTimeProperty("orderUpdatedAt", obj.order.updatedAt)
  }
}

object NestedChannelFormatter extends SingleRowFormatter[Channel] {
  override def writeRow(obj: Channel, json: JsonObject): Unit = {
    json.addProperty("id", obj.id.value)
    json.addProperty("name", obj.name)

    json.addStringArrayProperty("detailsContentTypes", obj.details.contentTypes.getOrElse(Nil))
    json.addStringArrayProperty("detailsContentCategories", obj.details.contentCategories.getOrElse(Nil))
    json.addProperty("detailsLanguage", obj.details.language.map(_.toLanguageTag).orNull)
    json.addProperty("detailsHubspotId", obj.details.hubspotId.orNull)
    json.addProperty("detailsContractId", obj.details.contractId.orNull)
    json.addProperty("detailsAwards", obj.details.awards.orNull)
    json.addProperty("detailsNotes", obj.details.notes.orNull)

    json.addProperty("ingestType", obj.ingest._type)
    json.addProperty("ingestDeliveryFrequency", obj.ingest.deliveryFrequency.map(_.toString))

    json.addStringArrayProperty("pedagogySubjects", obj.pedagogy.subjectNames.getOrElse(Nil))
    json.addProperty("pedagogyAgeRangeMin", obj.pedagogy.ageRangeMin.map(Int.box).orNull)
    json.addProperty("pedagogyAgeRangeMax", obj.pedagogy.ageRangeMax.map(Int.box).orNull)
    json.addStringArrayProperty("pedagogyBestForTags", obj.pedagogy.bestForTags.getOrElse(Nil))
    json.addProperty("pedagogyCurriculumAligned", obj.pedagogy.curriculumAligned.orNull)
    json.addProperty("pedagogyEducationalResources", obj.pedagogy.educationalResources.orNull)
    json.addProperty("pedagogyTranscriptProvided", obj.pedagogy.transcriptProvided.map(Boolean.box).orNull)

    json.addProperty("marketingStatus", obj.marketing.status.orNull)
    json.addProperty("marketingOneLineIntro", obj.marketing.oneLineIntro.orNull)
    json.addStringArrayProperty("marketingLogos", obj.marketing.logos.getOrElse(Nil))
    json.addProperty("marketingShowreel", obj.marketing.showreel.orNull)
    json.addStringArrayProperty("marketingSampleVideos", obj.marketing.sampleVideos.getOrElse(Nil))
  }
}

object VideoFormatter extends SingleRowFormatter[VideoWithRelatedData] {
  override def writeRow(obj: VideoWithRelatedData, json: JsonObject): Unit = {

    val VideoWithRelatedData(video, playbacks, orders, channel, impressions, interactions) = obj
    val highestResolutionAsset = video.largestAsset()

    json.addProperty("id", video.id.value)
    json.addProperty("contentPartner", video.contentPartner)
    json.addDateTimeProperty("ingestedAt", video.ingestedAt)

    val storageCharges = video.storageCharges(to = LocalDate.now()).map(StorageChargeFormatter.formatRow)
    json.addJsonArrayProperty("storageCharges", storageCharges)

    val ps = playbacks.map(p => PlaybackFormatter.formatRow(p))
    json.addJsonArrayProperty("playbacks", ps)

    val ordersJson = orders.map(o => NestedOrderFormatter.formatRow(o))
    json.addJsonArrayProperty(property = "orders", ordersJson)

    val channelJson: JsonObject = channel.map(NestedChannelFormatter.formatRow).orNull
    json.add("channel", channelJson)

    val impressionsJson = impressions.map(o => VideoSearchResultImpressionFormatter.formatRow(o))
    json.addJsonArrayProperty(property = "impressions", impressionsJson)

    val interactionsJson = interactions.map(o => VideoInteractionEventsFormatter.formatRow(o))
    json.addJsonArrayProperty(property = "interactions", interactionsJson)

    json.addProperty("playbackProvider", video.playbackProvider)
    json.addJsonArrayProperty("subjects", getAllSubjectsOf(video).map(SubjectFormatter.formatRow))
    json.addStringArrayProperty("ages", AgeFormatter.formatAges(video.ageRange))
    json.addProperty("durationSeconds", video.duration.getSeconds)
    json.addProperty("title", video.title)
    json.addProperty("type", video.contentType)
    json.addProperty("monthlyStorageCostGbp", video.monthlyStorageCostGbp())
    json.addProperty("storageCostSoFarGbp", video.storageCostSoFarGbp())
    json.addProperty("originalWidth", video.originalDimensions.map(d => d.width).getOrElse(0))
    json.addProperty("originalHeight", video.originalDimensions.map(d => d.height).getOrElse(0))
    json.addProperty("assetWidth", highestResolutionAsset.map(a => a.dimensions.width).getOrElse(0))
    json.addProperty("assetHeight", highestResolutionAsset.map(a => a.dimensions.height).getOrElse(0))
  }

  private def getAllSubjectsOf(video: Video): List[Subject] = {
    video.subjects match {
      case List() => List(Subject("UNKNOWN"))
      case subjects => subjects
    }
  }

}
