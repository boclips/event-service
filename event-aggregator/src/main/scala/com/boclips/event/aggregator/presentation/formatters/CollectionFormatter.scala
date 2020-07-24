package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.collections.Collection
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.boclips.event.aggregator.presentation.model.CollectionTableRow
import com.google.gson.JsonObject

object NestedCollectionFormatter extends SingleRowFormatter[Collection] {

  override def writeRow(obj: Collection, json: JsonObject): Unit = {
    json.addProperty("id", obj.id.value)
    json.addProperty("title", obj.title)
    json.addProperty("description", obj.description)
    json.addStringArrayProperty("subjects", obj.subjects)
    json.addStringArrayProperty("ages", AgeFormatter.formatAges(obj.ageRange))
    json.addStringArrayProperty("videoIds", obj.videoIds.map(_.value))
    json.addProperty("ownerId", obj.ownerId.value)
    json.addStringArrayProperty("bookmarks", obj.bookmarks.map(_.value))
    json.addDateTimeProperty("createdAt", obj.createdTime)
    json.addDateTimeProperty("updatedAt", obj.updatedTime)
    json.addProperty("deleted", obj.deleted)
    json.addProperty("public", obj.public)
    json.addProperty("promoted", obj.promoted)
  }
}


object CollectionFormatter extends SingleRowFormatter[CollectionTableRow] {

  override def writeRow(obj: CollectionTableRow, json: JsonObject): Unit = {
    val CollectionTableRow(collection, impressions, interactions) = obj

    NestedCollectionFormatter.extendRow(collection, json)

    val impressionsJson = impressions.map(o => CollectionSearchResultImpressionFormatter.formatRow(o))

    json.addJsonArrayProperty("impressions", impressionsJson)

    val interactionsJson = interactions.map(interaction => CollectionInteractionEventsFormatter.formatRow(interaction))

    json.addJsonArrayProperty("interactions", interactionsJson)
  }
}
