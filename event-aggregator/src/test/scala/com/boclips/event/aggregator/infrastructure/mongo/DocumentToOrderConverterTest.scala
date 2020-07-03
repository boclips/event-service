package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{Instant, ZoneOffset, ZonedDateTime}
import java.util.{Currency, Date}

import com.boclips.event.aggregator.domain.model.orders.{OrderId, OrderItem}
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.order.{OrderDocument, OrderItemDocument, OrderUserDocument}

import scala.collection.JavaConverters._
import scala.math.BigDecimal
class DocumentToOrderConverterTest extends Test {

  it should "convert id" in {
    val document = OrderDocument.sample().id("the-order-id").build()

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

  it should "convert requesting user" in {
    val document = OrderDocument.sample().requestingUser(OrderUserDocument.sample()
        .firstName("requester")
        .lastName("askingson")
        .email("e@mail.com")
        .legacyUserId("chevi-10")
        .label(null).build()).build()

    val order = DocumentToOrderConverter.convert(document)

    order.requestingUser.firstName should contain ("requester")
    order.requestingUser.lastName should contain ("askingson")
    order.requestingUser.email should contain ("e@mail.com")
    order.requestingUser.legacyUserId should contain ("chevi-10")
    order.requestingUser.label shouldBe None
  }

  it should "convert simple user" in {
    val document = OrderDocument.sample().requestingUser(OrderUserDocument.sample()
      .firstName(null)
      .lastName(null)
      .email(null)
      .legacyUserId(null)
      .label("red").build()).build()

    val order = DocumentToOrderConverter.convert(document)

    order.requestingUser.firstName shouldBe None
    order.requestingUser.lastName shouldBe None
    order.requestingUser.email shouldBe None
    order.requestingUser.legacyUserId shouldBe None
    order.requestingUser.label should contain ("red")
  }

  it should "convert authorising user" in {
    val document = OrderDocument.sample().authorisingUser(OrderUserDocument.sample()
      .firstName("requester")
      .lastName("askingson")
      .email("e@mail.com")
      .legacyUserId("chevi-10")
      .label(null).build()).build()

    val order = DocumentToOrderConverter.convert(document)

    order.authorisingUser.get.firstName should contain ("requester")
    order.authorisingUser.get.lastName should contain ("askingson")
    order.authorisingUser.get.email should contain ("e@mail.com")
    order.authorisingUser.get.legacyUserId should contain ("chevi-10")
    order.authorisingUser.get.label shouldBe None
  }

  it should "convert ISBN or Product number" in {
    val document = OrderDocument.sample().isbnOrProductNumber("pn-1").build()

    val order = DocumentToOrderConverter.convert(document)

    order.isbnOrProductNumber should contain ("pn-1")
  }

  it should "convert is through platform" in {
    val document = OrderDocument.sample().isThroughPlatform(true).build()

    val order = DocumentToOrderConverter.convert(document)

    order.isThroughPlatform shouldBe true
  }

  it should "convert currency when exists" in {
    val document = OrderDocument.sample().currency("USD").build()
    val order = DocumentToOrderConverter.convert(document)
    Option(order.currency).isInstanceOf[Currency]
    order.currency.map(_.getCurrencyCode) should contain ("USD")
  }

  it should "convert fxRate when exists" in {
    val document = OrderDocument.sample().fxRateToGbp(BigDecimal("10.0").bigDecimal).build()
    val order = DocumentToOrderConverter.convert(document)
    Option(order.fxRateToGbp).isInstanceOf[scala.math.BigDecimal]
    order.fxRateToGbp shouldBe Some(BigDecimal(10.0))
  }

  it should "deal with Null values" in {
    val document = OrderDocument.sample()
      .authorisingUser(null)
      .isbnOrProductNumber(null)
      .currency(null)
      .fxRateToGbp(null)
      .build()

    val order = DocumentToOrderConverter.convert(document)

    order.authorisingUser shouldBe None
    order.isbnOrProductNumber shouldBe None
    order.currency shouldBe None
    order.fxRateToGbp shouldBe None
  }
}
