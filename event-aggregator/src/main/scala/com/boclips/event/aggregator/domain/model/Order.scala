package com.boclips.event.aggregator.domain.model

import java.time.ZonedDateTime

import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.OrderFormatter

case class OrderId(value: String) extends Ordered[OrderId] {
  override def compare(that: OrderId): Int = value.compare(that.value)
}

case class OrderItem(
                      videoId: VideoId,
                      priceGbp: BigDecimal
                    )

case class Order(
                  id: OrderId,
                  createdAt: ZonedDateTime,
                  updatedAt: ZonedDateTime,
                  customerOrganisationName: String,
                  items: List[OrderItem]
                )

case class VideoItemWithOrder(item: OrderItem, order: Order)

object Order {
  implicit val formatter: RowFormatter[Order] = OrderFormatter
}
