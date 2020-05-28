package com.boclips.event.aggregator.presentation.formatters

import java.time.{Duration, Period, ZonedDateTime}
import java.util.Locale

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.createChannel
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory
import com.boclips.event.aggregator.testsupport.testfactories.OrderFactory.{createOrder, createOrderItem}
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearchRequest, createVideoSearchResultImpression}
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.{createVideo, createVideoAsset}


class VideoFormatterTest extends Test {
  it should "write subjects" in {
    val video = createVideo(
      subjects = List("maths", "physics")
    )

    val json = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    json.get("subjects").getAsJsonArray should have size 2
    json.getAsJsonArray("subjects").get(0).getAsJsonObject.getString("name") shouldBe "maths"
  }

  it should "set all the necessary fields" in {
    val video = createVideo(
      id = "the-id",
      contentPartner = "AP",
      playbackProvider = "YOUTUBE",
      subjects = List("Maths"),
      ageRange = AgeRange(Some(5), Some(6))
    )

    val jsonObjects = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObjects.get("id").getAsString shouldBe "the-id"
    jsonObjects.get("contentPartner").getAsString shouldBe "AP"
    jsonObjects.get("playbackProvider").getAsString shouldBe "YOUTUBE"
    jsonObjects.getStringList("ages") shouldBe List("05", "06")
  }

  it should "handle age open ranges" in {
    val video = createVideo(
      subjects = List("Math"),
      ageRange = AgeRange(Some(17), None)
    )

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())
    jsonObject.get("ages").getAsJsonArray should have size 3
  }

  it should "handle unspecified ranges" in {
    val video = createVideo(
      subjects = List("Math"),
      ageRange = AgeRange(None, None)
    )

    val jsonObjects = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObjects.getStringList("ages") shouldBe List("UNKNOWN")
  }

  it should "handle unspecified subjects" in {
    val video = createVideo(
      subjects = List()
    )

    val jsonObjects = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObjects.getAsJsonArray("subjects") should have size 1
    jsonObjects.getAsJsonArray("subjects").get(0).getAsJsonObject.getString("name") shouldBe "UNKNOWN"
  }

  it should "write video duration" in {
    val video = createVideo(duration = Duration.ofMinutes(2))

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.get("durationSeconds").getAsInt shouldBe 120
  }

  it should "write video title" in {
    val video = createVideo(title = "Video title")

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.get("title").getAsString shouldBe "Video title"
  }

  it should "write contentType when known" in {
    val video = createVideo(contentType = Some("STOCK"))

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.get("type").getAsString shouldBe "STOCK"
  }

  it should "write contentType when not known" in {
    val video = createVideo(contentType = None)

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.get("type").getAsString shouldBe "UNKNOWN"
  }

  it should "write ingestion timestamp" in {
    val video = createVideo(ingestedAt = ZonedDateTime.parse("2018-11-12T12:14:16.7Z[UTC]"))

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.get("ingestedAt").getAsString shouldBe "2018-11-12T12:14:16.7Z"
  }

  it should "write monthly storage cost in GBP" in {
    val video = createVideo(assets = List(createVideoAsset(sizeKb = 1000000)))

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.getDouble("monthlyStorageCostGbp") shouldBe 0.1
  }

  it should "write storage cost so far in GBP" in {
    val video = createVideo(assets = List(createVideoAsset(sizeKb = 1000000)), ingestedAt = ZonedDateTime.now().minusDays(365))

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.getDouble("storageCostSoFarGbp") shouldBe 1.2 +- 0.01
  }

  it should "write originalWidth & originHeight" in {
    val video = createVideo(originalDimensions = Some(Dimensions(480, 360)))

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.getInt("originalWidth") shouldBe 480
    jsonObject.getInt("originalHeight") shouldBe 360
  }

  it should "write original width & height as 0 when no data" in {
    val video = createVideo(originalDimensions = None)

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.getInt("originalWidth") shouldBe 0
    jsonObject.getInt("originalHeight") shouldBe 0
  }

  it should "write dimensions and size of the largest asset" in {
    val video = createVideo(assets = List(
      createVideoAsset(dimensions = Dimensions(480, 360), sizeKb = 1024),
      createVideoAsset(dimensions = Dimensions(1024, 860), sizeKb = 2048)
    ))

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.getInt("assetWidth") shouldBe 1024
    jsonObject.getInt("assetHeight") shouldBe 860
    jsonObject.getInt("assetSizeKb") shouldBe 2048
  }

  it should "write dimensions and size of the largest asset as 0 when no assets" in {
    val video = createVideo(assets = List())

    val jsonObject = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    jsonObject.getInt("assetWidth") shouldBe 0
    jsonObject.getInt("assetHeight") shouldBe 0
    jsonObject.getInt("assetSizeKb") shouldBe 0
  }

  it should "write nested storage charges" in {
    val video = createVideo(
      assets = List(createVideoAsset())
    )

    val json = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List())

    json.get("storageCharges").getAsJsonArray should have size 1
  }

  it should "write nested orders" in {
    val video = createVideo()
    val orders = List(VideoItemWithOrder(
      item = createOrderItem(priceGbp = BigDecimal(50)),
      order = createOrder(
        id = OrderId("orderId"),
        customerOrganisationName = "Pearson",
        createdAt = ZonedDateTime.parse("2010-10-20T00:00:00Z"),
        updatedAt = ZonedDateTime.parse("2010-10-21T00:00:00Z"),
      ))
    )

    val json = VideoFormatter formatRow VideoWithRelatedData(video, List(), orders, None, List(), List())

    val orderJson = json.get("orders").getAsJsonArray.get(0).getAsJsonObject
    orderJson.getString("id") should not be empty
    orderJson.getString("orderId") shouldBe "orderId"
    orderJson.getDouble("priceGbp") shouldBe 50
    orderJson.getString("customerOrganisationName") shouldBe "Pearson"
    orderJson.getString("orderCreatedAt") shouldBe "2010-10-20T00:00:00Z"
    orderJson.getString("orderUpdatedAt") shouldBe "2010-10-21T00:00:00Z"
  }

  it should "write nested channels" in {
    val video = createVideo(contentPartner = "cool channel")
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
        deliveryFrequency = Some(Period.ofMonths(2))
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

    val json = VideoFormatter formatRow VideoWithRelatedData(
      video,
      Nil,
      Nil,
      Some(channel),
      Nil,
      Nil
    )

    val channelJson = json.getAsJsonObject("channel")
    channelJson.getString("id") shouldBe "my-channel-id"
    channelJson.getString("name") shouldBe "cool channel"

    channelJson.getStringList("detailsContentTypes") shouldBe List("STOCK", "INSTRUCTIONAL")
    channelJson.getStringList("detailsContentCategories") shouldBe List("My category")
    channelJson.getString("detailsLanguage") shouldBe "fr-CA"
    channelJson.getString("detailsHubspotId") shouldBe "hubspot-id"
    channelJson.getString("detailsContractId") shouldBe "contract-id"
    channelJson.getString("detailsAwards") shouldBe "Awards text"
    channelJson.getString("detailsNotes") shouldBe "Notes text"

    channelJson.getString("ingestType") shouldBe "MRSS"
    channelJson.getString("ingestDeliveryFrequency") shouldBe "P2M"

    channelJson.getStringList("pedagogySubjects") shouldBe List("Maths")
    channelJson.getInt("pedagogyAgeRangeMin") shouldBe 5
    channelJson.getInt("pedagogyAgeRangeMax") shouldBe 10
    channelJson.getStringList("pedagogyBestForTags") shouldBe List("Hook")
    channelJson.getString("pedagogyCurriculumAligned") shouldBe "ks4"
    channelJson.getString("pedagogyEducationalResources") shouldBe "edu resources"
    channelJson.getBool("pedagogyTranscriptProvided") shouldBe true

    channelJson.getString("marketingStatus") shouldBe "my status"
    channelJson.getString("marketingOneLineIntro") shouldBe "my one liner"
    channelJson.getStringList("marketingLogos") shouldBe List("http://logo.com")
    channelJson.getString("marketingShowreel") shouldBe "http://showreel.com"
    channelJson.getStringList("marketingSampleVideos") shouldBe List("http://sampleVideos.com")
  }

  it should "write nested impressions" in {
    val video = createVideo()
    val impression = createVideoSearchResultImpression(
      search = createSearchRequest(
        timestamp = ZonedDateTime.parse("2020-10-20T10:11:12Z"),
        query = "maths",
      ),
      interaction = true,
    )

    val json = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(impression), List())

    val impressionJson = json.get("impressions").getAsJsonArray.get(0).getAsJsonObject
    impressionJson.getString("timestamp") shouldBe "2020-10-20T10:11:12Z"
    impressionJson.getBool("interaction") shouldBe true
    impressionJson.getString("query") shouldBe "maths"
    impressionJson.getString("id") should not be empty
  }

  it should "write nested events" in {
    val video = createVideo(id = "23")
    val interaction = EventFactory.createVideoInteractedWithEvent(
      videoId = "23",
      timestamp = ZonedDateTime.parse("2020-10-20T10:11:12Z"),
      query = Some("maths"),
      userId = "user-id",
      subtype = Some("COOL-EVENT"),

    )

    val json = VideoFormatter formatRow VideoWithRelatedData(video, List(), List(), None, List(), List(interaction))

    val interactionsJson = json.get("interactions").getAsJsonArray.get(0).getAsJsonObject
    interactionsJson.getString("timestamp") shouldBe "2020-10-20T10:11:12Z"
    interactionsJson.getString("subtype") shouldBe "COOL-EVENT"
    interactionsJson.getString("query") shouldBe "maths"
    interactionsJson.getString("id") should not be empty
  }
}
