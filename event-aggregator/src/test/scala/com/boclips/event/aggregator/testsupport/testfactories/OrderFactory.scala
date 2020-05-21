package com.boclips.event.aggregator.testsupport.testfactories

import java.time.ZonedDateTime
import java.util.Date

import com.boclips.event.aggregator.domain.model.{Order, OrderId, OrderItem, VideoId}
import org.bson.Document

import scala.collection.JavaConverters._

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

  def createOrderDocument(
                         id: String = "order-id",
                         status: String = "COMPLETED",
                         createdAt: ZonedDateTime = ZonedDateTime.now(),
                         updatedAt: ZonedDateTime = ZonedDateTime.now(),
                         customerOrganisationName: String = "customer organisation name",
                         items: List[OrderItem] = List()
                         ): Document = {
    val properties = Map[String, Object](
      ("_id", id),
      ("status", status),
      ("createdAt", Date.from(createdAt.toInstant)),
      ("updatedAt", Date.from(updatedAt.toInstant)),
      ("customerOrganisationName", customerOrganisationName),
      ("items", items.map(item => new Document(Map[String, Object](
        ("videoId", item.videoId.value),
        ("priceGbp", item.priceGbp.toString())
      ).asJava)).asJava),
    )
    new Document(properties.asJava)
  }

}
