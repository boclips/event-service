package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.ContractLegalRestrictionDocument
import com.boclips.event.infrastructure.contract.ContractDocument
import com.boclips.event.service.domain.ContractLegalRestrictionsRepository
import com.boclips.eventbus.domain.ContractLegalRestriction
import com.boclips.eventbus.domain.contract.Contract
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.litote.kmongo.document
import org.litote.kmongo.getCollection

class MongoContractLegalRestrictionsRepository(private val mongoClient: MongoClient): ContractLegalRestrictionsRepository {
    companion object : KLogging() {
        const val COLLECTION_NAME = "contract-legal-restrictions"
    }

    override fun save(contractLegalRestriction: ContractLegalRestriction) {
        try {
            val document = contractLegalRestriction.toDocument()
            mongoClient
                    .getDatabase(DatabaseConstants.DB_NAME)
                    .getCollection<ContractLegalRestrictionDocument>(COLLECTION_NAME)
                    .replaceOne(
                            Document("_id", document.id),
                            document,
                            ReplaceOptions().upsert(true)
                    )
        } catch (e: Exception) {
            logger.error(e) { "Error writing contractLegalRestriction with ID=${contractLegalRestriction.id}" }
        }
    }

    }

    fun ContractLegalRestriction.toDocument() : ContractLegalRestrictionDocument{
        return ContractLegalRestrictionDocument.builder()
                .id(id)
                .text(text)
                .build()
    }

