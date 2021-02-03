package com.boclips.event.aggregator.testsupport.testfactories

import java.time.ZonedDateTime
import java.util.Currency

import com.boclips.event.aggregator.domain.model.orders
import com.boclips.event.aggregator.domain.model.orders.{Order, OrderId, OrderItem, OrderUser}
import com.boclips.event.aggregator.domain.model.videos.VideoId

object OrderFactory {

  def createOrder(
                   id: OrderId = OrderId("order-id"),
                   legacyOrderId: Option[String] = Some("leg-id"),
                   createdAt: ZonedDateTime = ZonedDateTime.now(),
                   updatedAt: ZonedDateTime = ZonedDateTime.now(),
                   deliveredAt: Option[ZonedDateTime] = Some(ZonedDateTime.now()),
                   customerOrganisationName: String = "customer name",
                   items: List[OrderItem] = List(),
                   requestingUser: OrderUser = createOrderUser(),
                   authorisingUser: Option[OrderUser] = Some(createOrderUser()),
                   isbnOrProductNumber: Option[String] = Some("isbn"),
                   currency: Option[Currency] = Some(Currency.getInstance("USD")),
                   fxRateToGbp: Option[BigDecimal] = Some(10),
                   status: String = "status",
                   orderSource: String = "LEGACY"
                 ): Order = {
    orders.Order(
      id = id,
      legacyOrderId = legacyOrderId,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deliveredAt = deliveredAt,
      customerOrganisationName = customerOrganisationName,
      items = items,
      requestingUser = requestingUser,
      authorisingUser = authorisingUser,
      isbnOrProductNumber = isbnOrProductNumber,
      currency = currency,
      fxRateToGbp = fxRateToGbp,
      status = status,
      orderSource = orderSource
    )
  }

  def createOrderUser(
                       email: Option[String] = Some("cool@mail.com"),
                       firstName: Option[String] = Some("first"),
                       lastName: Option[String] = Some("last"),
                       legacyUserId: Option[String] = Some("legacy-user-id"),
                       label: Option[String] = Some("label"),
                     ): OrderUser = {
    orders.OrderUser(
      email = email,
      firstName = firstName,
      lastName = lastName,
      legacyUserId = legacyUserId,
      label = label,
    )
  }

  def createOrderItem(
                       videoId: VideoId = VideoId("video-id"),
                       priceGbp: BigDecimal = BigDecimal("10")
                     ): OrderItem = {
    OrderItem(videoId = videoId, priceGbp = priceGbp)
  }

}
