package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.orders.Order
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.{JsonArray, JsonObject}

object OrderFormatter extends SingleRowFormatter[Order] {

  override def writeRow(obj: Order, json: JsonObject): Unit = {
    val items = new JsonArray()
    obj.items.map(item => {
      val jsonItem = new JsonObject()
      jsonItem.addProperty("videoId", item.videoId.value)
      jsonItem.addProperty("priceGbp", item.priceGbp.toDouble)
      jsonItem
    }).foreach(items.add(_))

    json.addProperty("id", obj.id.value)
    json.addDateTimeProperty("createdAt", obj.createdAt)
    json.addDateTimeProperty("updatedAt", obj.updatedAt)
    json.addProperty("customerOrganisationName", obj.customerOrganisationName)
    json.add("items", items)
  }
}
