package com.boclips.event.aggregator.presentation.formatters

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{OrderId, OrderItem, VideoId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.OrderFactory.createOrder

class OrderFormatterTest extends Test {

  it should "write order id" in {
    val json = OrderFormatter formatRow createOrder(id = OrderId("order-id"))

    json.getString("id") shouldBe "order-id"
  }

  it should "write creation date" in {
    val json = OrderFormatter formatRow createOrder(createdAt = ZonedDateTime.parse("2019-11-20T20:21:22Z"))

    json.getString("createdAt") shouldBe "2019-11-20T20:21:22Z"
  }

  it should "write update date" in {
    val json = OrderFormatter formatRow createOrder(updatedAt = ZonedDateTime.parse("2020-11-20T20:21:22Z"))

    json.getString("updatedAt") shouldBe "2020-11-20T20:21:22Z"
  }

  it should "write customer organisation name" in {
    val json = OrderFormatter formatRow createOrder(customerOrganisationName = "org name")

    json.getString("customerOrganisationName") shouldBe "org name"
  }

  it should "write video items" in {
    val json = OrderFormatter formatRow createOrder(items = List(OrderItem(videoId = VideoId("video-id"), priceGbp = BigDecimal("20.30"))))

    json.getObjectList("items") should have length 1
    json.getObjectList("items").head.getString("videoId") shouldBe "video-id"
    json.getObjectList("items").head.getDouble("priceGbp") shouldBe 20.3
  }

}
