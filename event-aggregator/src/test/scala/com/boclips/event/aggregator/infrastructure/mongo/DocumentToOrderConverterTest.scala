package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{Instant, ZoneOffset, ZonedDateTime}
import java.util.Date

import com.boclips.event.aggregator.domain.model.{OrderId, OrderItem, VideoId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.order.{OrderDocument, OrderItemDocument}

import scala.collection.JavaConverters._

class DocumentToOrderConverterTest extends Test {

  it should "convert id" in {
    val document = OrderDocument.sample()._id("the-order-id").build()

    val order = DocumentToOrderConverter.convert(document)

    order.id shouldBe OrderId("the-order-id")
  }

  it should "convert creation date" in {
    val document = OrderDocument.sample().createdAt(Date.from(Instant.parse("2019-10-11T05:11:15Z"))).build()

    val order = DocumentToOrderConverter.convert(document)

    order.createdAt shouldBe ZonedDateTime.of(2019, 10, 11, 5, 11, 15, 0, ZoneOffset.UTC)
  }

  it should "convert update date" in {
    val document = OrderDocument.sample().updatedAt(Date.from(Instant.parse("2020-10-11T05:11:15Z"))).build()

    val order = DocumentToOrderConverter.convert(document)

    order.updatedAt shouldBe ZonedDateTime.of(2020, 10, 11, 5, 11, 15, 0, ZoneOffset.UTC)
  }

  it should "convert video items" in {
    val document = OrderDocument.sample()
      .items(
        List(
          OrderItemDocument.sample.videoId("the-video-id").priceGbp("10.10").build()
        ).asJava
      )
      .build()

    val order = DocumentToOrderConverter.convert(document)

    order.items should contain only OrderItem(videoId = VideoId("the-video-id"), priceGbp = BigDecimal("10.10"))
  }

  it should "convert customer organisation name" in {
    val document = OrderDocument.sample().customerOrganisationName("customer org").build()

    val order = DocumentToOrderConverter.convert(document)

    order.customerOrganisationName shouldBe "customer org"
  }

}
