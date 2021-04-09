package com.boclips.event.aggregator.presentation.formatters

import java.time.Period
import java.util.Locale

import com.boclips.event.aggregator.domain.model.contentpartners._
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.createChannel
import com.google.gson.JsonNull

class ChannelFormatterTest extends Test {
  it should "write full channel" in {
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
      ),
      marketing = ChannelMarketing(
        status = Some("my status"),
        oneLineIntro = Some("my one liner"),
        logos = Some(List("http://logo.com","logo2")),
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

    json.getString("marketingStatus") shouldBe "my status"
    json.getString("marketingOneLineIntro") shouldBe "my one liner"
    json.getStringList("marketingLogos") shouldBe List("http://logo.com","logo2")
    json.getString("marketingUniqueLogo") shouldBe "http://logo.com"
    json.getString("marketingShowreel") shouldBe "http://showreel.com"
    json.getStringList("marketingSampleVideos") shouldBe List("http://sampleVideos.com")
  }

  it should "write all-none channel" in {
    val channel = createChannel(
      id = "my-channel-id",
      name = "cool channel",
      details = ChannelDetails(
        contentTypes = None,
        contentCategories = None,
        language = None,
        hubspotId = None,
        contractId = None,
        awards = None,
        notes = None
      ),
      ingest = ChannelIngest(
        _type = "MRSS",
        deliveryFrequency = None,
        distributionMethods = None
      ),
      pedagogy = ChannelPedagogy(
        subjectNames = None,
        ageRangeMin = None,
        ageRangeMax = None,
        bestForTags = None
      ),
      marketing = ChannelMarketing(
        status = None,
        oneLineIntro = None,
        logos = None,
        showreel = None,
        sampleVideos = None
      )
    )

    val json = ChannelFormatter formatRow channel

    json.getString("id") shouldBe "my-channel-id"
    json.getString("name") shouldBe "cool channel"

    json.getStringList("detailsContentTypes") shouldBe List()
    json.getStringList("detailsContentCategories") shouldBe List()
    json.get("detailsLanguage") shouldBe JsonNull.INSTANCE
    json.get("detailsHubspotId") shouldBe JsonNull.INSTANCE
    json.get("detailsContractId") shouldBe JsonNull.INSTANCE
    json.get("detailsAwards") shouldBe JsonNull.INSTANCE
    json.get("detailsNotes") shouldBe JsonNull.INSTANCE

    json.getString("ingestType") shouldBe "MRSS"
    json.getString("ingestDeliveryFrequency") shouldBe "UNKNOWN"
    json.getStringList("ingestDistributionMethods") shouldBe List()

    json.getStringList("pedagogySubjects") shouldBe List()
    json.get("pedagogyAgeRangeMin") shouldBe JsonNull.INSTANCE
    json.get("pedagogyAgeRangeMax") shouldBe JsonNull.INSTANCE
    json.getStringList("pedagogyBestForTags") shouldBe List()

    json.get("marketingStatus") shouldBe JsonNull.INSTANCE
    json.get("marketingOneLineIntro") shouldBe JsonNull.INSTANCE
    json.getStringList("marketingLogos") shouldBe List()
    json.get("marketingUniqueLogo") shouldBe JsonNull.INSTANCE
    json.get("marketingShowreel") shouldBe JsonNull.INSTANCE
    json.getStringList("marketingSampleVideos") shouldBe List()
  }

  it should "deal with empty list of logos" in {
    val channel = createChannel(
      id = "my-channel-id",
      name = "cool channel",
      details = ChannelDetails(
        contentTypes = None,
        contentCategories = None,
        language = None,
        hubspotId = None,
        contractId = None,
        awards = None,
        notes = None
      ),
      ingest = ChannelIngest(
        _type = "MRSS",
        deliveryFrequency = None,
        distributionMethods = None
      ),
      pedagogy = ChannelPedagogy(
        subjectNames = None,
        ageRangeMin = None,
        ageRangeMax = None,
        bestForTags = None
      ),
      marketing = ChannelMarketing(
        status = None,
        oneLineIntro = None,
        logos = Some(List()),
        showreel = None,
        sampleVideos = None
      )
    )

    val json = ChannelFormatter formatRow channel

    json.getStringList("marketingLogos") shouldBe List()
    json.get("marketingUniqueLogo") shouldBe JsonNull.INSTANCE
  }
}
