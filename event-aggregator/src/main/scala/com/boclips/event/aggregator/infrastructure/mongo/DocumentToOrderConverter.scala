package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.Currency

import com.boclips.event.aggregator.domain.model.orders
import com.boclips.event.aggregator.domain.model.orders.{Order, OrderId, OrderItem, OrderUser}
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.infrastructure.order.{OrderDocument, OrderUserDocument}

import scala.collection.JavaConverters._

object DocumentToOrderConverter {

  def convert(document: OrderDocument): Order = {
    orders.Order(
      id = OrderId(document.getId),
      legacyOrderId = document.getLegacyOrderId,
      createdAt = ZonedDateTime.ofInstant(document.getCreatedAt.toInstant, ZoneOffset.UTC),
      updatedAt = ZonedDateTime.ofInstant(document.getUpdatedAt.toInstant, ZoneOffset.UTC),
      deliveryDate = Option(document.getDeliveryDate).map(date => ZonedDateTime.ofInstant(date.toInstant, ZoneOffset.UTC)),
      customerOrganisationName = document.getCustomerOrganisationName,
      items = document.getItems.asScala.toList.map(item => OrderItem(videoId = VideoId(item.getVideoId), priceGbp = BigDecimal(item.getPriceGbp))),
      requestingUser = convertOrderUser(document.getRequestingUser),
      authorisingUser = Option(document.getAuthorisingUser).map(convertOrderUser),
      isThroughPlatform = document.getIsThroughPlatform,
      orderSource = document.getOrderSource,
      isbnOrProductNumber = Option(document.getIsbnOrProductNumber),
      currency = Option(document.getCurrency).map(Currency.getInstance),
      fxRateToGbp = Option(document.getFxRateToGbp).map(BigDecimal(_)),
      status = document.getStatus,
    )
  }

  private def convertOrderUser(document: OrderUserDocument): OrderUser = {
    OrderUser(
      firstName = Option(document.getFirstName),
      lastName = Option(document.getLastName),
      email = Option(document.getEmail),
      legacyUserId = Option(document.getLegacyUserId),
      label = Option(document.getLabel),
    )
  }
}
