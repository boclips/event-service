package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.channel.ChannelDetailsDocument
import com.boclips.event.infrastructure.channel.ChannelDocument
import com.boclips.event.infrastructure.channel.ChannelIngestDocument
import com.boclips.event.infrastructure.channel.ChannelMarketingDocument
import com.boclips.event.infrastructure.channel.ChannelPedagogyDocument
import com.boclips.event.service.domain.ChannelRepository
import com.boclips.eventbus.domain.contentpartner.Channel
import com.boclips.eventbus.domain.contentpartner.ChannelIngestDetails
import com.boclips.eventbus.domain.contentpartner.ChannelMarketingDetails
import com.boclips.eventbus.domain.contentpartner.ChannelPedagogyDetails
import com.boclips.eventbus.domain.contentpartner.ChannelTopLevelDetails
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
        .build()

fun ChannelTopLevelDetails.toDocument(): ChannelDetailsDocument =
    ChannelDetailsDocument.builder()
        .contentTypes(contentTypes)
        .contentCategories(contentCategories)
        .language(language?.toLanguageTag())
        .hubspotId(hubspotId)
        .contractId(contractId)
        .awards(awards)
        .notes(notes)
        .build()

fun ChannelIngestDetails.toDocument(): ChannelIngestDocument =
    ChannelIngestDocument.builder()
        .type(type)
        .deliveryFrequency(deliveryFrequency?.toString())
        .build()

fun ChannelPedagogyDetails.toDocument(): ChannelPedagogyDocument =
    ChannelPedagogyDocument.builder()
        .subjectNames(subjects?.map { it.name })
        .ageRangeMin(ageRange?.min)
        .ageRangeMax(ageRange?.max)
        .bestForTags(bestForTags)
        .curriculumAligned(curriculumAligned)
        .educationalResources(educationalResources)
        .transcriptProvided(transcriptProvided)
        .build()

fun ChannelMarketingDetails.toDocument(): ChannelMarketingDocument =
    ChannelMarketingDocument.builder()
        .status(status)
        .oneLineIntro(oneLineIntro)
        .logos(logos)
        .showreel(showreel)
        .sampleVideos(sampleVideos)
        .build()
