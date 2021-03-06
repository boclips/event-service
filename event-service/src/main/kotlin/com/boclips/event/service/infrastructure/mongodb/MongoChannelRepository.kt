package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.channel.*
import com.boclips.event.service.domain.ChannelRepository
import com.boclips.eventbus.domain.category.CategoryWithAncestors
import com.boclips.eventbus.domain.contentpartner.Channel
import com.boclips.eventbus.domain.contentpartner.ChannelIngestDetails
import com.boclips.eventbus.domain.contentpartner.ChannelMarketingDetails
import com.boclips.eventbus.domain.contentpartner.ChannelPedagogyDetails
import com.boclips.eventbus.domain.contentpartner.ChannelTopLevelDetails
import com.boclips.eventbus.domain.contentpartner.DistributionMethod
import com.mongodb.MongoClient
import com.mongodb.client.model.ReplaceOptions
import mu.KLogging
import org.bson.Document
import org.litote.kmongo.getCollection

class MongoChannelRepository(private val mongoClient: MongoClient) : ChannelRepository {
    companion object : KLogging() {
        const val COLLECTION_NAME = "channels"
    }

    override fun save(channel: Channel) {
        try {
            val document = channel.toDocument()
            mongoClient
                .getDatabase(DatabaseConstants.DB_NAME)
                .getCollection<ChannelDocument>(COLLECTION_NAME)
                .replaceOne(
                    Document("_id", document.id),
                    document,
                    ReplaceOptions().upsert(true)
                )
        } catch (e: Exception) {
            logger.error(e) { "Error writing channel with ID=${channel.id.value}" }
        }
    }
}

fun Channel.toDocument(): ChannelDocument =
    ChannelDocument.builder()
        .id(id.value)
        .name(name)
        .details(details.toDocument())
        .ingest(ingest.toDocument())
        .pedagogy(pedagogy.toDocument())
        .marketing(marketing.toDocument())
        .categories((categories.map { it.toDocument()}).toSet())
        .build()

fun ChannelTopLevelDetails.toDocument(): ChannelDetailsDocument =
    ChannelDetailsDocument.builder()
        .contentTypes(contentTypes)
        .contentCategories(contentCategories)
        .language(language?.toLanguageTag())
        .contractId(contractId)
        .notes(notes)
        .build()

fun ChannelIngestDetails.toDocument(): ChannelIngestDocument =
    ChannelIngestDocument.builder()
        .type(type)
        .distributionMethods(
            distributionMethods?.map { method ->
                method?.let {
                    when (it) {
                        DistributionMethod.DOWNLOAD -> DistributionMethodDocument.DOWNLOAD
                        DistributionMethod.STREAM -> DistributionMethodDocument.STREAM
                    }
                }
            }?.toSet()
        )
        .build()

fun ChannelPedagogyDetails.toDocument(): ChannelPedagogyDocument =
    ChannelPedagogyDocument.builder()
        .subjectNames(subjects?.map { it.name })
        .ageRangeMin(ageRange?.min)
        .ageRangeMax(ageRange?.max)
        .bestForTags(bestForTags)
        .build()

fun ChannelMarketingDetails.toDocument(): ChannelMarketingDocument =
    ChannelMarketingDocument.builder()
        .status(status)
        .oneLineIntro(oneLineIntro)
        .logos(logos)
        .showreel(showreel)
        .sampleVideos(sampleVideos)
        .build()

fun CategoryWithAncestors.toDocument(): CategoryWithAncestorsDocument =
    CategoryWithAncestorsDocument.builder()
        .code(code)
        .description(description)
        .ancestors(ancestors)
        .build()

