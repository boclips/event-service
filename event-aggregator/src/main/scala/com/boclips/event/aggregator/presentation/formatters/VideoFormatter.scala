package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.orders.VideoItemWithOrder
import com.boclips.event.aggregator.domain.model.videos.Video
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.boclips.event.aggregator.presentation.model.VideoTableRow
import com.google.gson.JsonObject

import java.time.LocalDate
import java.util.UUID
import scala.collection.breakOut

object NestedOrderFormatter extends SingleRowFormatter[VideoItemWithOrder] {
  override def writeRow(obj: VideoItemWithOrder, json: JsonObject): Unit = {
    json.addProperty("id", obj.order.id.value + "_" + obj.item.videoId.value + "_" + UUID.randomUUID().toString)
    json.addProperty("orderId", obj.order.id.value)
    json.addProperty("legacyOrderId", obj.order.legacyOrderId)
    json.addProperty("priceGbp", obj.item.priceGbp)
    json.addProperty("customerOrganisationName", obj.order.customerOrganisationName)
    json.addDateTimeProperty("orderCreatedAt", obj.order.createdAt)
    json.addDateTimeProperty("orderUpdatedAt", obj.order.updatedAt)
    json.addDateTimeProperty("orderDeliveredAt", obj.order.deliveredAt.orNull)
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
    json.addProperty("orderSource", obj.order.orderSource)
    json.addProperty("isbnOrProductNumber", obj.order.isbnOrProductNumber)
    json.addProperty("currency", obj.order.currency.map(_.getCurrencyCode))
    json.addProperty("fxRateToGbp", obj.order.fxRateToGbp.getOrElse(BigDecimal(1.0)).toDouble)
    json.addProperty("status", obj.order.status)
  }
}

object VideoFormatter extends SingleRowFormatter[VideoTableRow] {
  override def writeRow(row: VideoTableRow, json: JsonObject): Unit = {
    val highestResolutionAsset = row.video.largestAsset()

    json.addProperty("id", row.video.id.value)
    json.addDateTimeProperty("ingestedAt", row.video.ingestedAt)

    val storageCharges = row.video.storageCharges(to = LocalDate.now()).map(StorageChargeFormatter.formatRow)
    json.addJsonArrayProperty("storageCharges", storageCharges)

    val ps = row.playbacks.map(PlaybackFormatter.formatRow)
    json.addJsonArrayProperty("playbacks", ps)

    val ordersJson = row.orders.map(NestedOrderFormatter.formatRow)
    json.addJsonArrayProperty(property = "orders", ordersJson)

    val channelJson: JsonObject = row.channel.map(ChannelFormatter.formatRow).orNull
    json.add("channel", channelJson)

    val contractJson: JsonObject = row.contract.map(ContractFormatter.formatRow).orNull
    json.add("contract", contractJson)

    val collectionsJson = row.collections.map(NestedCollectionFormatter.formatRow)
    json.addJsonArrayProperty(property = "collections", collectionsJson)

    val impressionsJson = row.impressions.map(VideoSearchResultImpressionFormatter.formatRow)
    json.addJsonArrayProperty(property = "impressions", impressionsJson)

    val interactionsJson = row.interactions.map(VideoInteractionEventsFormatter.formatRow)
    json.addJsonArrayProperty(property = "interactions", interactionsJson)

    val topicsJson = row.video.topics.map(VideoTopicThreeLevelFormatter.formatRow)
    json.addJsonArrayProperty(property = "topics", topicsJson)

    val taxonomyCategoriesJson = row.video.categories match {
      case None => Nil
      case Some(categoriesMap) => categoriesMap.flatMap(
        item => item._2.map(element => {
          val categoryJson = new JsonObject
          categoryJson.addProperty("code", element.code.orNull)
          categoryJson.addProperty("description", element.description.orNull)
          categoryJson.addProperty("categorySource", item._1)
          categoryJson.addStringArrayProperty("ancestors", element.ancestors.getOrElse(Nil).toList)
          categoryJson
        }
        )(breakOut)
      ).toList
    }
    json.addJsonArrayProperty("taxonomyCategories", taxonomyCategoriesJson)

    val youTubeStatsJson = row.youTubeStats.map(_.viewCount) match {
      case Some(count) =>
        val obj = new JsonObject
        obj.addProperty("viewCount", count)
        obj
      case _ => null
    }
    json.add("youTubeStats", youTubeStatsJson)

    json.addStringArrayProperty("contentPackageNames", row.contentPackageNames)
    json.addProperty("playbackProvider", row.video.playbackProvider)
    json.addJsonArrayProperty("subjects", getAllSubjectsOf(row.video).map(SubjectFormatter.formatRow))
    json.addStringArrayProperty("ages", AgeFormatter.formatAges(row.video.ageRange))
    json.addProperty("durationSeconds", row.video.duration.getSeconds)
    json.addProperty("title", row.video.title)
    json.addProperty("type", row.video.contentType)
    json.addProperty("monthlyStorageCostGbp", row.video.monthlyStorageCostGbp())
    json.addProperty("storageCostSoFarGbp", row.video.storageCostSoFarGbp())
    json.addProperty("originalWidth", row.video.originalDimensions.map(d => d.width).getOrElse(0))
    json.addProperty("originalHeight", row.video.originalDimensions.map(d => d.height).getOrElse(0))
    json.addProperty("assetWidth", highestResolutionAsset.map(a => a.dimensions.width).getOrElse(0))
    json.addProperty("assetHeight", highestResolutionAsset.map(a => a.dimensions.height).getOrElse(0))
    json.addProperty("assetSizeKb", highestResolutionAsset.map(a => a.sizeKb).getOrElse(0))
    json.addProperty("promoted", row.video.promoted)
    json.addProperty("sourceVideoReference", row.video.sourceVideoReference)
    json.addProperty("deactivated", row.video.deactivated)
  }

  private def getAllSubjectsOf(video: Video): List[Subject] = {
    video.subjects match {
      case List() => List(Subject("UNKNOWN"))
      case subjects => subjects
    }
  }

}
