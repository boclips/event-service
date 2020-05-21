package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.CollectionWithRelatedData
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object CollectionFormatter extends SingleRowFormatter[CollectionWithRelatedData] {

  override def writeRow(obj: CollectionWithRelatedData, json: JsonObject): Unit = {
    val CollectionWithRelatedData(collection, impressions, interactions) = obj

    json.addProperty("id", collection.id.value)
    json.addProperty("title", collection.title)
    json.addProperty("description", collection.description)
    json.addStringArrayProperty("subjects", collection.subjects)
    json.addStringArrayProperty("ages", AgeFormatter.formatAges(collection.ageRange))
    json.addStringArrayProperty("videoIds", collection.videoIds.map(_.value))
    json.addProperty("ownerId", collection.ownerId.value)
    json.addStringArrayProperty("bookmarks", collection.bookmarks.map(_.value))
    json.addDateTimeProperty("createdAt", collection.createdTime)
    json.addDateTimeProperty("updatedAt", collection.updatedTime)
    json.addProperty("deleted", collection.deleted)
    json.addProperty("public", collection.public)

    val impressionsJson = impressions.map(o => CollectionSearchResultImpressionFormatter.formatRow(o))

    json.addJsonArrayProperty("impressions", impressionsJson)

    val interactionsJson = interactions.map(interaction => CollectionInteractionEventsFormatter.formatRow(interaction))

    json.addJsonArrayProperty("interactions", interactionsJson)

  }
}
