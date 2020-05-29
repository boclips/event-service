package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.order.OrderDocument
import com.boclips.event.infrastructure.order.OrderItemDocument
import com.boclips.event.service.domain.OrderRepository
import com.boclips.eventbus.events.order.Order
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
        write(
            OrderDocument.builder()
                .id(order.id)
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
}