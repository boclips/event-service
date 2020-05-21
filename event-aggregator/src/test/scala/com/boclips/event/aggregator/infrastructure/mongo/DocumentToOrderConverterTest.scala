package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{OrderId, OrderItem, VideoId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.OrderFactory.createOrderDocument

class DocumentToOrderConverterTest extends Test {

  it should "convert id" in {
    val document = createOrderDocument(id = "the-order-id")

    val order = DocumentToOrderConverter.convert(document)

    order.id shouldBe OrderId("the-order-id")
  }

  it should "convert creation date" in {
    val document = createOrderDocument(createdAt = ZonedDateTime.parse("2019-10-11T05:11:15Z"))

    val order = DocumentToOrderConverter.convert(document)

    order.createdAt shouldBe ZonedDateTime.of(2019, 10, 11, 5, 11, 15, 0, ZoneOffset.UTC)
  }

  it should "convert update date" in {
    val document = createOrderDocument(updatedAt = ZonedDateTime.parse("2020-10-11T05:11:15Z"))

    val order = DocumentToOrderConverter.convert(document)

    order.updatedAt shouldBe ZonedDateTime.of(2020, 10, 11, 5, 11, 15, 0, ZoneOffset.UTC)
  }

  it should "convert video items" in {
    val document = createOrderDocument(items = List(OrderItem(videoId = VideoId("the-video-id"), priceGbp = BigDecimal("10.10"))))

    val order = DocumentToOrderConverter.convert(document)

    order.items should contain only OrderItem(videoId = VideoId("the-video-id"), priceGbp = BigDecimal("10.10"))
  }

  it should "convert customer organisation name" in {
    val document = createOrderDocument(customerOrganisationName = "customer org")

    val order = DocumentToOrderConverter.convert(document)

    order.customerOrganisationName shouldBe "customer org"
  }

}
