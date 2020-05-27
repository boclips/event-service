package com.boclips.event.aggregator.testsupport.testfactories

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.{Order, OrderId, OrderItem, VideoId}

object OrderFactory {

  def createOrder(
                   id: OrderId = OrderId("order-id"),
                   createdAt: ZonedDateTime = ZonedDateTime.now(),
                   updatedAt: ZonedDateTime = ZonedDateTime.now(),
                   customerOrganisationName: String = "customer name",
                   items: List[OrderItem] = List(),
                 ): Order = {
    Order(
      id = id,
      createdAt = createdAt,
      updatedAt = updatedAt,
      customerOrganisationName = customerOrganisationName,
      items = items,
    )
  }

  def createOrderItem(
                       videoId: VideoId = VideoId("video-id"),
                       priceGbp: BigDecimal = BigDecimal("10")
                     ): OrderItem = {
    OrderItem(videoId = videoId, priceGbp = priceGbp)
  }

}
