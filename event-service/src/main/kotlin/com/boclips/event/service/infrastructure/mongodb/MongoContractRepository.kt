package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.channel.ChannelDocument
import com.boclips.event.infrastructure.contract.ContractCostsDocument
import com.boclips.event.infrastructure.contract.ContractDatesDocument
import com.boclips.event.infrastructure.contract.ContractDocument
import com.boclips.event.infrastructure.contract.ContractRestrictionsDocument
import com.boclips.event.infrastructure.contract.ContractRoyaltySplitDocument
import com.boclips.event.service.domain.ContractRepository
import com.boclips.eventbus.domain.contract.Contract
import com.boclips.eventbus.domain.contract.ContractCosts
import com.boclips.eventbus.domain.contract.ContractDates
import com.boclips.eventbus.domain.contract.ContractRestrictions
import com.boclips.eventbus.domain.contract.ContractRoyaltySplit
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.litote.kmongo.getCollection

class MongoContractRepository(private val mongoClient: MongoClient) : ContractRepository {
    companion object : KLogging() {
        const val COLLECTION_NAME = "contracts"
    }

    override fun save(contract: Contract) {
        try {
            val document = contract.toDocument()
            mongoClient
                .getDatabase(DatabaseConstants.DB_NAME)
                .getCollection<ContractDocument>(COLLECTION_NAME)
                .replaceOne(
                    Document("_id", document.id),
                    document,
                    ReplaceOptions().upsert(true)
                )
        } catch (e: Exception) {
            logger.error(e) { "Error writing contract with ID=${contract.contractId.value}" }
        }
    }
}

fun Contract.toDocument(): ContractDocument =
    ContractDocument.builder()
        .id(contractId.value)
        .channelName(name)
        .contractDocumentLink(contractDocument)
        .contractIsRolling(contractIsRolling)
        .contractDates(contractDates.toDocument())
        .daysBeforeTerminationWarning(daysBeforeTerminationWarning)
        .yearsForMaximumLicense(yearsForMaximumLicense)
        .daysForSellOffPeriod(daysForSellOffPeriod)
        .royaltySplit(royaltySplit.toDocument())
        .minimumPriceDescription(minimumPriceDescription)
        .remittanceCurrency(remittanceCurrency.currencyCode)
        .restrictions(restrictions.toDocument())
        .costs(costs.toDocument())
        .build()

fun ContractDates.toDocument(): ContractDatesDocument =
    ContractDatesDocument.builder()
        .start(start)
        .end(end)
        .build()

fun ContractRoyaltySplit.toDocument(): ContractRoyaltySplitDocument =
    ContractRoyaltySplitDocument.builder()
        .download(download)
        .streaming(streaming)
        .build()

fun ContractRestrictions.toDocument(): ContractRestrictionsDocument =
    ContractRestrictionsDocument.builder()
        .clientFacing(clientFacing)
        .territory(territory)
        .licensing(licensing)
        .editing(editing)
        .marketing(marketing)
        .companies(companies)
        .payout(payout)
        .other(other)
        .build()

fun ContractCosts.toDocument(): ContractCostsDocument =
    ContractCostsDocument.builder()
        .minimumGuarantee(minimumGuarantee)
        .upfrontLicense(upfrontLicense)
        .technicalFee(technicalFee)
        .recoupable(recoupable)
        .build()