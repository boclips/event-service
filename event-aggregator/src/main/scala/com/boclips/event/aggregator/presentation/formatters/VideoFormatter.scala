package com.boclips.event.aggregator.presentation.formatters

import java.time.LocalDate

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.orders.VideoItemWithOrder
import com.boclips.event.aggregator.domain.model.videos.Video
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.boclips.event.aggregator.presentation.model.VideoTableRow
import com.google.gson.{JsonArray, JsonObject}

object NestedOrderFormatter extends SingleRowFormatter[VideoItemWithOrder] {
  override def writeRow(obj: VideoItemWithOrder, json: JsonObject): Unit = {
    json.addProperty("id", obj.order.id.value + "_" + obj.item.videoId.value)
    json.addProperty("orderId", obj.order.id.value)
    json.addProperty("priceGbp", obj.item.priceGbp)
    json.addProperty("customerOrganisationName", obj.order.customerOrganisationName)
    json.addDateTimeProperty("orderCreatedAt", obj.order.createdAt)
    json.addDateTimeProperty("orderUpdatedAt", obj.order.updatedAt)
    json.addProperty("authorisingUserFirstName", obj.order.authorisingUser.flatMap(_.firstName))
    json.addProperty("authorisingUserLastName", obj.order.authorisingUser.flatMap(_.lastName))
    json.addProperty("authorisingUserEmail", obj.order.authorisingUser.flatMap(_.email))
    json.addProperty("authorisingUserLegacyUserId", obj.order.authorisingUser.flatMap(_.legacyUserId))
    json.addProperty("authorisingUserLabel", obj.order.authorisingUser.flatMap(_.label))
    json.addProperty("requestingUserFirstName", obj.order.requestingUser.firstName)
    json.addProperty("requestingUserLastName", obj.order.requestingUser.lastName)
    json.addProperty("requestingUserEmail", obj.order.requestingUser.email)
    json.addProperty("requestingUserLegacyUserId", obj.order.requestingUser.legacyUserId)
    json.addProperty("requestingUserLabel", obj.order.requestingUser.label)
    json.addProperty("isThroughPlatform", obj.order.isThroughPlatform)
    json.addProperty("isbnOrProductNumber", obj.order.isbnOrProductNumber)
    json.addProperty("currency", obj.order.currency.map(_.getCurrencyCode))
    json.addProperty("fxRateToGbp", obj.order.fxRateToGbp.getOrElse(BigDecimal(1.0)).toDouble)
  }
}

object VideoFormatter extends SingleRowFormatter[VideoTableRow] {
  override def writeRow(obj: VideoTableRow, json: JsonObject): Unit = {

    val VideoTableRow(video, playbacks, orders, channel, contract, collections, impressions, interactions) = obj
    val highestResolutionAsset = video.largestAsset()

    json.addProperty("id", video.id.value)
    json.addDateTimeProperty("ingestedAt", video.ingestedAt)

    val storageCharges = video.storageCharges(to = LocalDate.now()).map(StorageChargeFormatter.formatRow)
    json.addJsonArrayProperty("storageCharges", storageCharges)

    val ps = playbacks.map(p => PlaybackFormatter.formatRow(p))
    json.addJsonArrayProperty("playbacks", ps)

    val ordersJson = orders.map(o => NestedOrderFormatter.formatRow(o))
    json.addJsonArrayProperty(property = "orders", ordersJson)

    val channelJson: JsonObject = channel.map(ChannelFormatter.formatRow).orNull
    json.add("channel", channelJson)

    val contractJson: JsonObject = contract.map(ContractFormatter.formatRow).orNull
    json.add("contract", contractJson)

    val collectionsJson = collections.map(o => NestedCollectionFormatter.formatRow(o))
    json.addJsonArrayProperty(property = "collections", collectionsJson)

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
    json.addProperty("assetSizeKb", highestResolutionAsset.map(a => a.sizeKb).getOrElse(0))
    json.addProperty("promoted", video.promoted)
  }

  private def getAllSubjectsOf(video: Video): List[Subject] = {
    video.subjects match {
      case List() => List(Subject("UNKNOWN"))
      case subjects => subjects
    }
  }

}
