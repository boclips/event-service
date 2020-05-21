package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{Order, OrderId, OrderItem, VideoId}
import org.bson.Document

object DocumentToOrderConverter {

  def convert(document: Document): Order = {
    Order(
      id = OrderId(document.getString("_id")),
      createdAt = ZonedDateTime.ofInstant(document.getDate("createdAt").toInstant, ZoneOffset.UTC),
      updatedAt = ZonedDateTime.ofInstant(document.getDate("updatedAt").toInstant, ZoneOffset.UTC),
      customerOrganisationName = document.getString("customerOrganisationName"),
      items = document.getList[Document]("items").map(item => OrderItem(videoId = VideoId(item.getString("videoId")), priceGbp = BigDecimal(item.getString("priceGbp"))))
    )
  }

}
