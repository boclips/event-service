package com.boclips.event.aggregator.presentation.formatters

import java.time.LocalDate

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.orders.VideoItemWithOrder
import com.boclips.event.aggregator.domain.model.videos.Video
import com.boclips.event.aggregator.presentation.VideoWithRelatedData
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.{JsonArray, JsonElement, JsonObject}

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

object VideoFormatter extends SingleRowFormatter[VideoWithRelatedData] {
  override def writeRow(obj: VideoWithRelatedData, json: JsonObject): Unit = {

    val VideoWithRelatedData(video, playbacks, orders, channel, contract, impressions, interactions) = obj
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
  }

  private def getAllSubjectsOf(video: Video): List[Subject] = {
    video.subjects match {
      case List() => List(Subject("UNKNOWN"))
      case subjects => subjects
    }
  }

}
