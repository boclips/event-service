package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.contentpartner.ChannelIngestDetails
import com.boclips.eventbus.domain.contentpartner.ChannelMarketingDetails
import com.boclips.eventbus.domain.contentpartner.ChannelPedagogyDetails
import com.boclips.eventbus.domain.contentpartner.ChannelTopLevelDetails
import com.boclips.eventbus.domain.contentpartner.ContentPartner
import com.boclips.eventbus.domain.contentpartner.ContentPartnerId
import java.time.Period
import java.util.Locale

object ChannelFactory {
    fun createChannel(
        id: String = "my-channel-id",
        name: String = "my channel name",
        details: ChannelTopLevelDetails = createChannelTopLevelDetails(),
        pedagogy: ChannelPedagogyDetails = createChannelPedagogyDetails(),
        ingest: ChannelIngestDetails = createChannelIngestDetails(),
        marketing: ChannelMarketingDetails = createChannelMarketingDetails()
    ): ContentPartner =
        ContentPartner.builder()
            .id(ContentPartnerId.builder().value(id).build())
            .name(name)
            .ageRange(AgeRange.builder().min(null).max(null).build())
            .details(details)
            .pedagogy(pedagogy)
            .ingest(ingest)
            .marketing(marketing)
            .build()

    fun createChannelTopLevelDetails(
        contentTypes: List<String>? = null,
        contentCategories: List<String>? = null,
        language: Locale? = null,
        hubspotId: String? = null,
        contractId: String? = null,
        awards: String? = null,
        notes: String? = null
    ): ChannelTopLevelDetails =
        ChannelTopLevelDetails
            .builder()
            .contentTypes(contentTypes)
            .contentCategories(contentCategories)
            .language(language)
            .hubspotId(hubspotId)
            .contractId(contractId)
            .awards(awards)
            .notes(notes)
            .build()

    fun createChannelMarketingDetails(
        status: String? = null,
        oneLineIntro: String? = null,
        logos: List<String>? = null,
        showreel: String? = null,
        sampleVideos: List<String>? = null
    ): ChannelMarketingDetails =
        ChannelMarketingDetails.builder()
            .status(status)
            .oneLineIntro(oneLineIntro)
            .logos(logos)
            .showreel(showreel)
            .sampleVideos(sampleVideos)
            .build()

    fun createChannelIngestDetails(
        type: String = "MRSS",
        deliveryFrequency: Period? = null
    ): ChannelIngestDetails =
        ChannelIngestDetails.builder()
            .type(type)
            .deliveryFrequency(deliveryFrequency)
            .build()

    fun createChannelPedagogyDetails(
        subjects: List<Subject>? = null,
        ageRange: AgeRange = AgeRange.builder().min(6).max(12).build(),
        bestForTags: List<String>? = null,
        curriculumAligned: String? = null,
        educationalResources: String? = null,
        transcriptProvided: Boolean? = null
    ): ChannelPedagogyDetails =
        ChannelPedagogyDetails.builder()
            .subjects(subjects)
            .ageRange(ageRange)
            .bestForTags(bestForTags)
            .curriculumAligned(curriculumAligned)
            .educationalResources(educationalResources)
            .transcriptProvided(transcriptProvided)
            .build()
}