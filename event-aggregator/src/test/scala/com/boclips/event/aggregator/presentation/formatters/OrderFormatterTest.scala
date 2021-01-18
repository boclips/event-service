package com.boclips.event.aggregator.presentation.formatters

import java.time.ZonedDateTime
import java.util.Currency

import com.boclips.event.aggregator.domain.model.orders.{OrderId, OrderItem}
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.OrderFactory.{createOrder, createOrderUser}

class OrderFormatterTest extends Test {

  it should "write order id" in {
    val json = OrderFormatter formatRow createOrder(id = OrderId("order-id"))

    json.getString("id") shouldBe "order-id"
  }

  it should "write legacy order id" in {
    val json = OrderFormatter formatRow createOrder(legacyOrderId = "order-1984")

    json.getString("legacyOrderId") shouldBe "order-1984"
  }

  it should "write creation date" in {
    val json = OrderFormatter formatRow createOrder(createdAt = ZonedDateTime.parse("2019-11-20T20:21:22Z"))

    json.getString("createdAt") shouldBe "2019-11-20T20:21:22Z"
  }

  it should "write update date" in {
    val json = OrderFormatter formatRow createOrder(updatedAt = ZonedDateTime.parse("2020-11-20T20:21:22Z"))

    json.getString("updatedAt") shouldBe "2020-11-20T20:21:22Z"
  }

  it should "write delivery date" in {
      val json = OrderFormatter formatRow createOrder(deliveryDate = Some(ZonedDateTime.parse("2020-11-20T20:21:22Z")))

      json.getString("deliveryDate") shouldBe "2020-11-20T20:21:22Z"
    }

  it should "write customer organisation name" in {
    val json = OrderFormatter formatRow createOrder(customerOrganisationName = "org name")

    json.getString("customerOrganisationName") shouldBe "org name"
  }

  it should "write order status" in {
    val json = OrderFormatter formatRow createOrder(orderSource = "LEGACY")

    json.getString("orderSource") shouldBe "LEGACY"
  }

  it should "write video items" in {
    val json = OrderFormatter formatRow createOrder(items = List(OrderItem(videoId = VideoId("video-id"), priceGbp = BigDecimal("20.30"))))

    json.getObjectList("items") should have length 1
    json.getObjectList("items").head.getString("videoId") shouldBe "video-id"
    json.getObjectList("items").head.getDouble("priceGbp") shouldBe 20.3
  }

  it should "write authorising user details" in {
    val json = OrderFormatter formatRow createOrder(authorisingUser = Some(createOrderUser(
      firstName = Some("Bryan"),
      lastName = Some("Adams"),
      email = Some("ba@rock.com"),
      legacyUserId = Some("luid"),
      label = None,
    )))

    json.getString("authorisingUserFirstName") shouldBe "Bryan"
    json.getString("authorisingUserLastName") shouldBe "Adams"
    json.getString("authorisingUserEmail") shouldBe "ba@rock.com"
    json.getString("authorisingUserLegacyUserId") shouldBe "luid"
    json.getString("authorisingUserLabel") shouldBe "UNKNOWN"
  }

  it should "write requesting user details" in {
    val json = OrderFormatter formatRow createOrder(requestingUser = createOrderUser(
      firstName = Some("Bryan"),
      lastName = Some("Adams"),
      email = Some("ba@rock.com"),
      legacyUserId = Some("luid"),
      label = None,
    ))

    json.getString("requestingUserFirstName") shouldBe "Bryan"
    json.getString("requestingUserLastName") shouldBe "Adams"
    json.getString("requestingUserEmail") shouldBe "ba@rock.com"
    json.getString("requestingUserLegacyUserId") shouldBe "luid"
    json.getString("requestingUserLabel") shouldBe "UNKNOWN"
  }
  it should "write extra properties" in {
    val json = OrderFormatter formatRow createOrder(
      isbnOrProductNumber = Some("covid-19"),
      isThroughPlatform = false,
      currency = Some(Currency.getInstance("USD")),
      fxRateToGbp = Some(BigDecimal(10.0)),
    )

    json.getString("isbnOrProductNumber") shouldBe "covid-19"
    json.getString("currency") shouldBe "USD"
    json.get("fxRateToGbp").getAsDouble shouldBe 10.0
    json.getBool("isThroughPlatform") shouldBe false

  }

  it should "handle nulls gracefully" in {
    val json = OrderFormatter formatRow createOrder(
      isbnOrProductNumber = None,
      currency = None,
      fxRateToGbp = None,
      authorisingUser = None
    )

    json.getString("isbnOrProductNumber") shouldBe "UNKNOWN"
    json.getString("currency") shouldBe "UNKNOWN"
    json.get("fxRateToGbp").getAsDouble shouldBe 1
    json.getString("authorisingUserFirstName") shouldBe "UNKNOWN"
  }
}
