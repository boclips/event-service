package com.boclips.event.aggregator.infrastructure.mongo

import java.time.Period
import java.util.Locale

import com.boclips.event.aggregator.domain.model.ChannelId
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.{createChannelDetailsDocument, createChannelDocument, createChannelIngestDocument, createChannelMarketingDocument, createChannelPedagogyDocument}

class DocumentToChannelConverterTest extends Test {
  it should "covert a full document" in {
    val document = createChannelDocument(
      id = "this-channel-id",
      name = "this channel name",
      details = createChannelDetailsDocument(
        contentTypes = Some(List("STOCK", "NEWS")),
        contentCategories = Some(List("My category")),
        language = Some("fr-CA"),
        hubspotId = Some("hubspot-id"),
        contractId = Some("contract-id"),
        awards = Some("Awards text"),
        notes = Some("Notes text"),
      ),
      ingest = createChannelIngestDocument(
        _type = "MANUAL",
        deliveryFrequency = Some("P1M"),
      ),
      pedagogy = createChannelPedagogyDocument(
        subjectNames = Some(List("ENGLISH", "MATH")),
        ageRangeMin = Some(8),
        ageRangeMax = Some(16),
        bestForTags = Some(List("cool", "tag")),
        curriculumAligned = Some("Curriculum text"),
        educationalResources = Some("Educational resources text"),
        transcriptProvided = Some(true),
      ),
      marketing = createChannelMarketingDocument(
        status = Some("Status"),
        oneLineIntro = Some("One line intro"),
        logos = Some(List("http://logo1.com", "http://logo2.com")),
        showreel = Some("http://showreel.com"),
        sampleVideos = Some(List("http://video1.com", "http://video2.com")),
      )
    )

    val channel = DocumentToChannelConverter convert document

    channel.id shouldBe ChannelId("this-channel-id")
    channel.name shouldBe "this channel name"

    channel.details.contentTypes should contain(List("STOCK", "NEWS"))
    channel.details.contentCategories should contain(List("My category"))
    channel.details.language should contain(Locale.CANADA_FRENCH)
    channel.details.hubspotId should contain("hubspot-id")
    channel.details.contractId should contain("contract-id")
    channel.details.awards should contain("Awards text")
    channel.details.notes should contain("Notes text")

    channel.ingest._type shouldBe "MANUAL"
    channel.ingest.deliveryFrequency should contain(Period.ofMonths(1))

    channel.pedagogy.subjectNames should contain(List("ENGLISH", "MATH"))
    channel.pedagogy.ageRangeMin should contain(8)
    channel.pedagogy.ageRangeMax should contain(16)
    channel.pedagogy.bestForTags should contain(List("cool", "tag"))
    channel.pedagogy.curriculumAligned should contain("Curriculum text")
    channel.pedagogy.educationalResources should contain("Educational resources text")
    channel.pedagogy.transcriptProvided should contain(true)

    channel.marketing.status should contain("Status")
    channel.marketing.oneLineIntro should contain("One line intro")
    channel.marketing.logos should contain(List("http://logo1.com", "http://logo2.com"))
    channel.marketing.showreel should contain("http://showreel.com")
    channel.marketing.sampleVideos should contain(List("http://video1.com", "http://video2.com"))
  }
}
