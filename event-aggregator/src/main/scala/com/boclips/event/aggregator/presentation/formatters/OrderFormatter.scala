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
    json.addProperty("legacyOrderId", obj.legacyOrderId)
    json.addDateTimeProperty("createdAt", obj.createdAt)
    json.addDateTimeProperty("updatedAt", obj.updatedAt)
    json.addProperty("customerOrganisationName", obj.customerOrganisationName)
    json.addProperty("authorisingUserFirstName", obj.authorisingUser.flatMap(_.firstName))
    json.addProperty("authorisingUserLastName", obj.authorisingUser.flatMap(_.lastName))
    json.addProperty("authorisingUserEmail", obj.authorisingUser.flatMap(_.email))
    json.addProperty("authorisingUserLegacyUserId", obj.authorisingUser.flatMap(_.legacyUserId))
    json.addProperty("authorisingUserLabel", obj.authorisingUser.flatMap(_.label))
    json.addProperty("requestingUserFirstName", obj.requestingUser.firstName)
    json.addProperty("requestingUserLastName", obj.requestingUser.lastName)
    json.addProperty("requestingUserEmail", obj.requestingUser.email)
    json.addProperty("requestingUserLegacyUserId", obj.requestingUser.legacyUserId)
    json.addProperty("requestingUserLabel", obj.requestingUser.label)
    json.addProperty("isThroughPlatform",obj.isThroughPlatform)
    json.addProperty("isbnOrProductNumber",obj.isbnOrProductNumber)
    json.addProperty("currency",obj.currency.map(_.getCurrencyCode))
    json.addProperty("fxRateToGbp", obj.fxRateToGbp.getOrElse(BigDecimal(1.0)).toDouble)
    json.add("items", items)
  }
}
