package com.boclips.event.aggregator.presentation.formatters

import java.time.Period
import java.util.Locale

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.createChannel
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideo

class ChannelFormatterTest extends Test {
  it should "write nested channel" in {
    val channel = createChannel(
      id = "my-channel-id",
      name = "cool channel",
      details = ChannelDetails(
        contentTypes = Some(List("STOCK", "INSTRUCTIONAL")),
        contentCategories = Some(List("My category")),
        language = Some(Locale.CANADA_FRENCH),
        hubspotId = Some("hubspot-id"),
        contractId = Some("contract-id"),
        awards = Some("Awards text"),
        notes = Some("Notes text")
      ),
      ingest = ChannelIngest(
        _type = "MRSS",
        deliveryFrequency = Some(Period.ofMonths(2)),
        distributionMethods = Some(Set(Streaming, Download))
      ),
      pedagogy = ChannelPedagogy(
        subjectNames = Some(List("Maths")),
        ageRangeMin = Some(5),
        ageRangeMax = Some(10),
        bestForTags = Some(List("Hook")),
        curriculumAligned = Some("ks4"),
        educationalResources = Some("edu resources"),
        transcriptProvided = Some(true),
      ),
      marketing = ChannelMarketing(
        status = Some("my status"),
        oneLineIntro = Some("my one liner"),
        logos = Some(List("http://logo.com")),
        showreel = Some("http://showreel.com"),
        sampleVideos = Some(List("http://sampleVideos.com"))
      )
    )

    val json = ChannelFormatter formatRow channel

    json.getString("id") shouldBe "my-channel-id"
    json.getString("name") shouldBe "cool channel"

    json.getStringList("detailsContentTypes") shouldBe List("STOCK", "INSTRUCTIONAL")
    json.getStringList("detailsContentCategories") shouldBe List("My category")
    json.getString("detailsLanguage") shouldBe "fr-CA"
    json.getString("detailsHubspotId") shouldBe "hubspot-id"
    json.getString("detailsContractId") shouldBe "contract-id"
    json.getString("detailsAwards") shouldBe "Awards text"
    json.getString("detailsNotes") shouldBe "Notes text"

    json.getString("ingestType") shouldBe "MRSS"
    json.getString("ingestDeliveryFrequency") shouldBe "P2M"
    json.getStringList("ingestDistributionMethods") shouldBe List("STREAM", "DOWNLOAD")

    json.getStringList("pedagogySubjects") shouldBe List("Maths")
    json.getInt("pedagogyAgeRangeMin") shouldBe 5
    json.getInt("pedagogyAgeRangeMax") shouldBe 10
    json.getStringList("pedagogyBestForTags") shouldBe List("Hook")
    json.getString("pedagogyCurriculumAligned") shouldBe "ks4"
    json.getString("pedagogyEducationalResources") shouldBe "edu resources"
    json.getBool("pedagogyTranscriptProvided") shouldBe true

    json.getString("marketingStatus") shouldBe "my status"
    json.getString("marketingOneLineIntro") shouldBe "my one liner"
    json.getStringList("marketingLogos") shouldBe List("http://logo.com")
    json.getString("marketingShowreel") shouldBe "http://showreel.com"
    json.getStringList("marketingSampleVideos") shouldBe List("http://sampleVideos.com")
  }
}