package com.boclips.event.aggregator.domain.model.orders

import java.time.ZonedDateTime
import java.util.Currency

import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.OrderFormatter

case class OrderId(value: String) extends Ordered[OrderId] {
  override def compare(that: OrderId): Int = value.compare(that.value)
}

case class OrderItem(
                      videoId: VideoId,
                      priceGbp: BigDecimal
                    )

case class OrderUser(
                      email: Option[String],
                      firstName: Option[String],
                      lastName: Option[String],
                      legacyUserId: Option[String],
                      label: Option[String],
                    )

case class Order(
                  id: OrderId,
                  legacyOrderId: String,
                  createdAt: ZonedDateTime,
                  updatedAt: ZonedDateTime,
                  deliveryDate: Option[ZonedDateTime],
                  customerOrganisationName: String,
                  items: List[OrderItem],
                  requestingUser: OrderUser,
                  authorisingUser: Option[OrderUser],
                  isThroughPlatform: Boolean,
                  orderSource: String,
                  isbnOrProductNumber: Option[String],
                  currency: Option[Currency],
                  fxRateToGbp: Option[BigDecimal],
                  status: String,
                )

case class VideoItemWithOrder(item: OrderItem, order: Order)

object Order {
  implicit val formatter: RowFormatter[Order] = OrderFormatter
}
