package com.boclips.event.aggregator.infrastructure.mongo

import java.time.Period
import java.util.Locale

import com.boclips.event.aggregator.domain.model.ChannelId
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.channel._

import scala.collection.JavaConverters._

class DocumentToChannelConverterTest extends Test {
  it should "covert a full document" in {
    val document = ChannelDocument.sample
      .id("this-channel-id")
      .name("this channel name")
      .details(
        ChannelDetailsDocument.sample
          .contentTypes(List("STOCK", "NEWS").asJava)
          .contentCategories(List("My category").asJava)
          .language("fr-CA")
          .hubspotId("hubspot-id")
          .contractId("contract-id")
          .awards("Awards text")
          .notes("Notes text")
          .build()
      )
      .ingest(
        ChannelIngestDocument.sample
          .`type`("MANUAL")
          .deliveryFrequency("P1M")
          .build()
      )
      .pedagogy(
        ChannelPedagogyDocument.sample
          .subjectNames(List("ENGLISH", "MATH").asJava)
          .ageRangeMin(8)
          .ageRangeMax(16)
          .bestForTags(List("cool", "tag").asJava)
          .curriculumAligned("Curriculum text")
          .educationalResources("Educational resources text")
          .transcriptProvided(true)
          .build()
      )
      .marketing(
        ChannelMarketingDocument.sample
          .status("Status")
          .oneLineIntro("One line intro")
          .logos(List("http://logo1.com", "http://logo2.com").asJava)
          .showreel("http://showreel.com")
          .sampleVideos(List("http://video1.com", "http://video2.com").asJava)
          .build()
      )
      .build()

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
