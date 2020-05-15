package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.service.domain.ChannelRepository
import com.boclips.eventbus.domain.contentpartner.ChannelIngestDetails
import com.boclips.eventbus.domain.contentpartner.ChannelMarketingDetails
import com.boclips.eventbus.domain.contentpartner.ChannelPedagogyDetails
import com.boclips.eventbus.domain.contentpartner.ChannelTopLevelDetails
import com.boclips.eventbus.domain.contentpartner.ContentPartner
import com.mongodb.MongoClient
import mu.KLogging
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

class MongoChannelRepository(private val mongoClient: MongoClient) : ChannelRepository {
    companion object : KLogging() {
        const val COLLECTION_NAME = "channels"
    }

    override fun save(channel: ContentPartner) {
        try {
            mongoClient
                .getDatabase(DatabaseConstants.DB_NAME)
                .getCollection<ChannelDocument>(COLLECTION_NAME)
                .save(channel.toDocument())
        } catch (e: Exception) {
            logger.error(e) { "Error writing channel with ID=${channel.id.value}" }
        }
    }
}

fun ContentPartner.toDocument() =
    ChannelDocument(
        id = id.value,
        name = name,
        details = details.toDocument(),
        ingest = ingest.toDocument(),
        pedagogy = pedagogy.toDocument(),
        marketing = marketing.toDocument()
    )

fun ChannelTopLevelDetails.toDocument() =
    ChannelTopLevelDocument(
        contentTypes = contentTypes,
        contentCategories = contentCategories,
        language = language?.toLanguageTag(),
        hubspotId = hubspotId,
        contractId = contractId,
        awards = awards,
        notes = notes
    )

fun ChannelIngestDetails.toDocument() =
    ChannelIngestDocument(
        type = type,
        deliveryFrequency = deliveryFrequency?.toString()
    )

fun ChannelPedagogyDetails.toDocument() =
    ChannelPedagogyDocument(
        subjectNames = subjects?.map { it.name },
        ageRangeMin = ageRange?.min,
        ageRangeMax = ageRange?.max,
        bestForTags = bestForTags,
        curriculumAligned = curriculumAligned,
        educationalResources = educationalResources,
        transcriptProvided = transcriptProvided
    )

fun ChannelMarketingDetails.toDocument() =
    ChannelMarketingDocument(
        status = status,
        oneLineIntro = oneLineIntro,
        logos = logos,
        showreel = showreel,
        sampleVideos = sampleVideos
    )

data class ChannelDocument(
    @BsonId
    val id: String,
    val name: String,
    val details: ChannelTopLevelDocument,
    val ingest: ChannelIngestDocument,
    val pedagogy: ChannelPedagogyDocument,
    val marketing: ChannelMarketingDocument
)

data class ChannelTopLevelDocument(
    val contentTypes: List<String>?,
    val contentCategories: List<String>?,
    val language: String?,
    val hubspotId: String?,
    val contractId: String?,
    val awards: String?,
    val notes: String?
)

data class ChannelIngestDocument(
    val type: String,
    val deliveryFrequency: String?
)

data class ChannelPedagogyDocument(
    val subjectNames: List<String>?,
    val ageRangeMin: Int?,
    val ageRangeMax: Int?,
    val bestForTags: List<String>?,
    val curriculumAligned: String?,
    val educationalResources: String?,
    val transcriptProvided: Boolean?
)

data class ChannelMarketingDocument(
    val status: String?,
    val oneLineIntro: String?,
    val logos: List<String>?,
    val showreel: String?,
    val sampleVideos: List<String>?
)