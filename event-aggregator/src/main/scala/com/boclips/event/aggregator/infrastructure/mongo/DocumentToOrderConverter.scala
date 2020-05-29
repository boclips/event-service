package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{Order, OrderId, OrderItem, VideoId}
import com.boclips.event.infrastructure.order.OrderDocument

import scala.collection.JavaConverters._

object DocumentToOrderConverter {

  def convert(document: OrderDocument): Order = {
    Order(
      id = OrderId(document.getId),
      createdAt = ZonedDateTime.ofInstant(document.getCreatedAt.toInstant, ZoneOffset.UTC),
      updatedAt = ZonedDateTime.ofInstant(document.getUpdatedAt.toInstant, ZoneOffset.UTC),
      customerOrganisationName = document.getCustomerOrganisationName,
      items = document.getItems.asScala.toList.map(item => OrderItem(videoId = VideoId(item.getVideoId), priceGbp = BigDecimal(item.getPriceGbp)))
    )
  }

}
