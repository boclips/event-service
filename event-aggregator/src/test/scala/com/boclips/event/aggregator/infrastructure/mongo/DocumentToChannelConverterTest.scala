package com.boclips.event.aggregator.infrastructure.mongo

import java.time.Period
import java.util.Locale

import com.boclips.event.aggregator.domain.model.contentpartners.{ChannelId, Download, Streaming}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.channel._

import scala.collection.JavaConverters._

class DocumentToChannelConverterTest extends Test {
  it should "convert a full document" in {
    val document = ChannelDocument.sample
      .id("this-channel-id")
      .name("this channel name")
      .details(
        ChannelDetailsDocument.sample
          .contentTypes(List("STOCK", "NEWS").asJava)
          .contentCategories(List("My category").asJava)
          .language("fr-CA")
          .contractId("contract-id")
          .notes("Notes text")
          .build()
      )
      .ingest(
        ChannelIngestDocument.sample
          .`type`("MANUAL")
          .deliveryFrequency("P1M")
          .distributionMethods(Set(
            DistributionMethodDocument.STREAM,
            DistributionMethodDocument.DOWNLOAD
          ).asJava)
          .build()
      )
      .pedagogy(
        ChannelPedagogyDocument.sample
          .subjectNames(List("ENGLISH", "MATH").asJava)
          .ageRangeMin(8)
          .ageRangeMax(16)
          .bestForTags(List("cool", "tag").asJava)
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
    channel.details.contractId should contain("contract-id")
    channel.details.notes should contain("Notes text")

    channel.ingest._type shouldBe "MANUAL"
    channel.ingest.deliveryFrequency should contain(Period.ofMonths(1))
    channel.ingest.distributionMethods should contain(Set(Streaming, Download))

    channel.pedagogy.subjectNames should contain(List("ENGLISH", "MATH"))
    channel.pedagogy.ageRangeMin should contain(8)
    channel.pedagogy.ageRangeMax should contain(16)
    channel.pedagogy.bestForTags should contain(List("cool", "tag"))

    channel.marketing.status should contain("Status")
    channel.marketing.oneLineIntro should contain("One line intro")
    channel.marketing.logos should contain(List("http://logo1.com", "http://logo2.com"))
    channel.marketing.showreel should contain("http://showreel.com")
    channel.marketing.sampleVideos should contain(List("http://video1.com", "http://video2.com"))
  }

  it should "convert an as-null-as-possible document" in {
    val document = ChannelDocument.sample
      .id("this-channel-id")
      .name("this channel name")
      .details(
        ChannelDetailsDocument.sample
          .contentTypes(null)
          .contentCategories(null)
          .language(null)
          .contractId(null)
          .notes(null)
          .build()
      )
      .ingest(
        ChannelIngestDocument.sample
          .`type`("MANUAL")
          .deliveryFrequency(null)
          .build()
      )
      .pedagogy(
        ChannelPedagogyDocument.sample
          .subjectNames(null)
          .ageRangeMin(null)
          .ageRangeMax(null)
          .bestForTags(null)
          .build()
      )
      .marketing(
        ChannelMarketingDocument.sample
          .status(null)
          .oneLineIntro(null)
          .logos(null)
          .showreel(null)
          .sampleVideos(null)
          .build()
      )
      .build()

    val channel = DocumentToChannelConverter convert document

    channel.id shouldBe ChannelId("this-channel-id")
    channel.name shouldBe "this channel name"

    channel.details.contentTypes shouldBe None
    channel.details.contentCategories shouldBe None
    channel.details.language shouldBe None
    channel.details.contractId shouldBe None
    channel.details.notes shouldBe None

    channel.ingest._type shouldBe "MANUAL"
    channel.ingest.deliveryFrequency shouldBe None

    channel.pedagogy.subjectNames shouldBe None
    channel.pedagogy.ageRangeMin shouldBe None
    channel.pedagogy.ageRangeMax shouldBe None
    channel.pedagogy.bestForTags shouldBe None

    channel.marketing.status shouldBe None
    channel.marketing.oneLineIntro shouldBe None
    channel.marketing.logos shouldBe None
    channel.marketing.showreel shouldBe None
    channel.marketing.sampleVideos shouldBe None
  }
}
