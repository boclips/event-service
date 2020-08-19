package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.order.OrderDocument
import com.boclips.event.infrastructure.order.OrderItemDocument
import com.boclips.event.infrastructure.order.OrderUserDocument
import com.boclips.event.service.domain.OrderRepository
import com.boclips.eventbus.events.order.Order
import com.boclips.eventbus.events.order.OrderUser
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.litote.kmongo.getCollection
import java.util.Date

class MongoOrderRepository(private val mongoClient: MongoClient) : OrderRepository {

    companion object : KLogging() {
        const val COLLECTION_NAME = "orders"
    }

    override fun saveOrder(order: Order) {
        val requestingUserDoc = order.requestingUser?.let(this::orderUserDocument)
        val authorisingUserDoc = order.authorisingUser?.let(this::orderUserDocument)
        write(
            OrderDocument.builder()
                .id(order.id)
                    .legacyOrderId(order.legacyOrderId)
                .status(order.status?.name ?: "UNKNOWN")
                .createdAt(Date.from(order.createdAt.toInstant()))
                .updatedAt(Date.from(order.updatedAt.toInstant()))
                .customerOrganisationName(order.customerOrganisationName)
                .items(
                    order.items.map {
                        OrderItemDocument.builder()
                            .videoId(it.videoId.value)
                            .priceGbp(it.priceGbp.toPlainString())
                            .build()
                    }
                )
                    .requestingUser(requestingUserDoc)
                    .authorisingUser(authorisingUserDoc)
                    .fxRateToGbp(order.fxRateToGbp)
                    .currency(order.currency.currencyCode)
                    .isThroughPlatform(order.isThroughPlatform)
                    .isbnOrProductNumber(order.isbnOrProductNumber)
                .build()
        )
    }

    private fun write(document: OrderDocument) {
        try {
            getCollection().replaceOne(Document("_id", document.id), document, ReplaceOptions().upsert(true))
        } catch (e: Exception) {
            logger.error(e) { "Error writing order ${document.id}" }
        }
    }

    private fun getCollection() =
        mongoClient.getDatabase(DatabaseConstants.DB_NAME).getCollection<OrderDocument>(COLLECTION_NAME)

    private fun orderUserDocument(user: OrderUser): OrderUserDocument {
        return OrderUserDocument.builder()
                .email(user.email)
                .firstName(user.firstName)
                .lastName(user.lastName)
                .legacyUserId(user.legacyUserId)
                .label(user.label)
                .build()
    }
}