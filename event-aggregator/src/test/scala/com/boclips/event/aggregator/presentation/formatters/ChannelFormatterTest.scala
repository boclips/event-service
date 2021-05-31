package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.contentpartners._
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.createChannel
import com.google.gson.JsonNull

import java.time.Period
import java.util.Locale

class ChannelFormatterTest extends Test {
  it should "write full channel" in {
    val channel = createChannel(
      id = "my-channel-id",
      name = "cool channel",
      details = ChannelDetails(
        contentTypes = Some(List("STOCK", "INSTRUCTIONAL")),
        contentCategories = Some(List("My category")),
        language = Some(Locale.CANADA_FRENCH),
        contractId = Some("contract-id"),
        notes = Some("Notes text")
      ),
      ingest = ChannelIngest(
        _type = "MRSS",
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
      ),
      categories = Some(Set(CategoryWithAncestors(
        code = Some("AA"),
        description = Some("Antelopes"),
        ancestors = Some(Set("AA","PP")))
        )
      )
    )

    val json = ChannelFormatter formatRow channel

    json.getString("id") shouldBe "my-channel-id"
    json.getString("name") shouldBe "cool channel"

    json.getStringList("detailsContentTypes") shouldBe List("STOCK", "INSTRUCTIONAL")
    json.getStringList("detailsContentCategories") shouldBe List("My category")
    json.getString("detailsLanguage") shouldBe "fr-CA"
    json.getString("detailsContractId") shouldBe "contract-id"
    json.getString("detailsNotes") shouldBe "Notes text"

    json.getString("ingestType") shouldBe "MRSS"
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


    val categoriesJson = json.get("taxonomyCategories").getAsJsonArray.get(0).getAsJsonObject
    categoriesJson.getString("code") shouldBe("AA")
    categoriesJson.getString("description") shouldBe("Antelopes")
    categoriesJson.getStringList("ancestors") shouldBe List("AA","PP")
  }

  it should "write all-none channel" in {
    val channel = createChannel(
      id = "my-channel-id",
      name = "cool channel",
      details = ChannelDetails(
        contentTypes = None,
        contentCategories = None,
        language = None,
        contractId = None,
        notes = None
      ),
      ingest = ChannelIngest(
        _type = "MRSS",
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
      ),
      categories = None
    )

    val json = ChannelFormatter formatRow channel

    json.getString("id") shouldBe "my-channel-id"
    json.getString("name") shouldBe "cool channel"

    json.getStringList("detailsContentTypes") shouldBe List()
    json.getStringList("detailsContentCategories") shouldBe List()
    json.get("detailsLanguage") shouldBe JsonNull.INSTANCE
    json.get("detailsContractId") shouldBe JsonNull.INSTANCE
    json.get("detailsNotes") shouldBe JsonNull.INSTANCE

    json.getString("ingestType") shouldBe "MRSS"
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
    json.getAsJsonArray("taxonomyCategories").size() shouldBe 0

  }

  it should "deal with empty list of logos" in {
    val channel = createChannel(
      id = "my-channel-id",
      name = "cool channel",
      details = ChannelDetails(
        contentTypes = None,
        contentCategories = None,
        language = None,
        contractId = None,
        notes = None
      ),
      ingest = ChannelIngest(
        _type = "MRSS",
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

  it should "deal with taxonomy category null values" in {
    val channel = createChannel(
      categories = Some(Set(CategoryWithAncestors(None,None,None)))
    )

    val json = ChannelFormatter formatRow channel

    val categoriesJson = json.get("taxonomyCategories").getAsJsonArray.get(0).getAsJsonObject
    categoriesJson.get("code") shouldBe JsonNull.INSTANCE
    categoriesJson.get("description") shouldBe JsonNull.INSTANCE
    categoriesJson.getStringList("ancestors") shouldBe List()


  }
}
