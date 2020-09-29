package com.boclips.event.aggregator.presentation.formatters

import java.time.{Duration, LocalDate, Period, ZonedDateTime}
import java.util.{Currency, Locale}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.contentpartners._
import com.boclips.event.aggregator.domain.model.orders.{OrderId, VideoItemWithOrder}
import com.boclips.event.aggregator.domain.model.videos.{Dimensions, VideoId, VideoTopic, YouTubeVideoStats}
import com.boclips.event.aggregator.presentation.model
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.createChannel
import com.boclips.event.aggregator.testsupport.testfactories.CollectionFactory.createCollection
import com.boclips.event.aggregator.testsupport.testfactories.ContractFactory.{createContractRestriction, createFullContract, createFullTableRowContract}
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory
import com.boclips.event.aggregator.testsupport.testfactories.OrderFactory.{createOrder, createOrderItem, createOrderUser}
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.{createSearchRequest, createVideoSearchResultImpression}
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.{createVideo, createVideoAsset}

import scala.collection.JavaConverters._


class VideoFormatterTest extends Test {
  it should "write subjects" in {
    val video = createVideo(
      subjects = List("maths", "physics")
    )

    val json = VideoFormatter formatRow model.VideoTableRow(video)

    json.get("subjects").getAsJsonArray should have size 2
    json.getAsJsonArray("subjects").get(0).getAsJsonObject.getString("name") shouldBe "maths"
  }

  it should "set all the necessary fields" in {
    val video = createVideo(
      id = "the-id",
      playbackProvider = "YOUTUBE",
      subjects = List("Maths"),
      ageRange = AgeRange(Some(5), Some(6))
    )

    val jsonObjects = VideoFormatter formatRow model.VideoTableRow(video)


    jsonObjects.get("id").getAsString shouldBe "the-id"
    jsonObjects.get("playbackProvider").getAsString shouldBe "YOUTUBE"
    jsonObjects.getStringList("ages") shouldBe List("05", "06")
  }

  it should "handle age open ranges" in {
    val video = createVideo(
      subjects = List("Math"),
      ageRange = AgeRange(Some(17), None)
    )

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)
    jsonObject.get("ages").getAsJsonArray should have size 3
  }

  it should "handle unspecified ranges" in {
    val video = createVideo(
      subjects = List("Math"),
      ageRange = AgeRange(None, None)
    )

    val jsonObjects = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObjects.getStringList("ages") shouldBe List("UNKNOWN")
  }

  it should "handle unspecified subjects" in {
    val video = createVideo(
      subjects = List()
    )

    val jsonObjects = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObjects.getAsJsonArray("subjects") should have size 1
    jsonObjects.getAsJsonArray("subjects").get(0).getAsJsonObject.getString("name") shouldBe "UNKNOWN"
  }

  it should "write video duration" in {
    val video = createVideo(duration = Duration.ofMinutes(2))

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.get("durationSeconds").getAsInt shouldBe 120
  }

  it should "write video title" in {
    val video = createVideo(title = "Video title")

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.get("title").getAsString shouldBe "Video title"
  }

  it should "write contentType when known" in {
    val video = createVideo(contentType = Some("STOCK"))

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.get("type").getAsString shouldBe "STOCK"
  }

  it should "write contentType when not known" in {
    val video = createVideo(contentType = None)

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.get("type").getAsString shouldBe "UNKNOWN"
  }

  it should "write promoted flag" in {
    val video = createVideo(promoted = true)

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.get("promoted").getAsBoolean shouldBe true
  }

  it should "write release and ingestion time info" in {
    val video = createVideo(
      releasedOn = LocalDate.parse("2016-10-02"),
      ingestedAt = ZonedDateTime.parse("2018-11-12T12:14:16.7Z[UTC]")
    )

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.get("ingestedAt").getAsString shouldBe "2018-11-12T12:14:16.7Z"
  }

  it should "write monthly storage cost in GBP" in {
    val video = createVideo(assets = List(createVideoAsset(sizeKb = 1000000)))

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.getDouble("monthlyStorageCostGbp") shouldBe 0.1
  }

  it should "write storage cost so far in GBP" in {
    val video = createVideo(assets = List(createVideoAsset(sizeKb = 1000000)), ingestedAt = ZonedDateTime.now().minusDays(365))

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.getDouble("storageCostSoFarGbp") shouldBe 1.2 +- 0.01
  }

  it should "write originalWidth & originHeight" in {
    val video = createVideo(originalDimensions = Some(Dimensions(480, 360)))

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.getInt("originalWidth") shouldBe 480
    jsonObject.getInt("originalHeight") shouldBe 360
  }

  it should "write original width & height as 0 when no data" in {
    val video = createVideo(originalDimensions = None)

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.getInt("originalWidth") shouldBe 0
    jsonObject.getInt("originalHeight") shouldBe 0
  }

  it should "write dimensions and size of the largest asset" in {
    val video = createVideo(assets = List(
      createVideoAsset(dimensions = Dimensions(480, 360), sizeKb = 1024),
      createVideoAsset(dimensions = Dimensions(1024, 860), sizeKb = 2048)
    ))

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.getInt("assetWidth") shouldBe 1024
    jsonObject.getInt("assetHeight") shouldBe 860
    jsonObject.getInt("assetSizeKb") shouldBe 2048
  }

  it should "write dimensions and size of the largest asset as 0 when no assets" in {
    val video = createVideo(assets = List())

    val jsonObject = VideoFormatter formatRow model.VideoTableRow(video)

    jsonObject.getInt("assetWidth") shouldBe 0
    jsonObject.getInt("assetHeight") shouldBe 0
    jsonObject.getInt("assetSizeKb") shouldBe 0
  }

  it should "write nested storage charges" in {
    val video = createVideo(
      assets = List(createVideoAsset())
    )

    val json = VideoFormatter formatRow model.VideoTableRow(video)

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
        requestingUser = createOrderUser(
          firstName = Some("Bryan"),
          lastName = Some("Adams"),
          email = Some("ba@rock.com"),
          legacyUserId = Some("luid"),
          label = None,
        ),
        isbnOrProductNumber = Some("covid-19"),
        isThroughPlatform = false,
        currency = Some(Currency.getInstance("USD")),
        fxRateToGbp = Some(BigDecimal(10.0)),

      ))
    )

    val json = VideoFormatter formatRow model.VideoTableRow(video = video, orders = orders)

    val orderJson = json.get("orders").getAsJsonArray.get(0).getAsJsonObject
    orderJson.getString("id") should not be empty
    orderJson.getString("orderId") shouldBe "orderId"
    orderJson.getDouble("priceGbp") shouldBe 50
    orderJson.getString("customerOrganisationName") shouldBe "Pearson"
    orderJson.getString("orderCreatedAt") shouldBe "2010-10-20T00:00:00Z"
    orderJson.getString("orderUpdatedAt") shouldBe "2010-10-21T00:00:00Z"
    orderJson.getString("requestingUserFirstName") shouldBe "Bryan"
    orderJson.getString("requestingUserLastName") shouldBe "Adams"
    orderJson.getString("requestingUserEmail") shouldBe "ba@rock.com"
    orderJson.getString("requestingUserLegacyUserId") shouldBe "luid"
    orderJson.getString("requestingUserLabel") shouldBe "UNKNOWN"
    orderJson.getString("isbnOrProductNumber") shouldBe "covid-19"
    orderJson.getString("currency") shouldBe "USD"
    orderJson.get("fxRateToGbp").getAsDouble shouldBe 10.0
    orderJson.getBool("isThroughPlatform") shouldBe false
  }

  it should "write nested order null values gracefully" in {
    val video = createVideo()
    val orders = List(VideoItemWithOrder(
      item = createOrderItem(priceGbp = BigDecimal(50)),
      order = createOrder(
        isbnOrProductNumber = None,
        currency = None,
        fxRateToGbp = None,
        authorisingUser = None

      ))
    )

    val json = VideoFormatter formatRow model.VideoTableRow(video = video, orders = orders)

    val orderJson = json.get("orders").getAsJsonArray.get(0).getAsJsonObject
    orderJson.getString("isbnOrProductNumber") shouldBe "UNKNOWN"
    orderJson.getString("currency") shouldBe "UNKNOWN"
    orderJson.get("fxRateToGbp").getAsDouble shouldBe 1
    orderJson.getString("authorisingUserFirstName") shouldBe "UNKNOWN"
  }

  it should "write nested channel" in {
    val video = createVideo()
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

    val json = VideoFormatter formatRow model.VideoTableRow(
      video = video,
      channel = Some(channel),
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
    channelJson.getStringList("ingestDistributionMethods") shouldBe List("STREAM", "DOWNLOAD")

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

  it should "write nested collection" in {
    val video = createVideo()
    val collections = List(createCollection(id = "1"), createCollection(id = "2"))
    val json = VideoFormatter formatRow model.VideoTableRow(video = video, collections = collections)

    val collectionsJson = json.getAsJsonArray("collections")
    collectionsJson.size() shouldBe 2
  }

  it should "write nested contract" in {
    val video = createVideo()
    val contract = createFullTableRowContract(createFullContract(
      id = "my-contract-id",
      channelName = "my channel name",
      contractDocumentLink = "http://mysite.com",
      contractIsRolling = false,
      contractDates = ContractDates(
        start = Some(LocalDate.ofYearDay(2008, 1)),
        end = Some(LocalDate.ofYearDay(2021, 360))
      ),
      daysBeforeTerminationWarning = 300,
      yearsForMaximumLicense = 1,
      daysForSellOffPeriod = 101,
      royaltySplit = ContractRoyaltySplit(
        download = Some(18.1F),
        streaming = Some(90F)
      ),
      minimumPriceDescription = "minimum price",
      remittanceCurrency = Currency.getInstance("USD"),
      restrictions = ContractRestrictions(
        clientFacing = Some(List("client-facing")),
        territory = Some("territory"),
        licensing = Some("licensing"),
        editing = Some("editing"),
        marketing = Some("marketing"),
        companies = Some("companies"),
        payout = Some("payout"),
        other = Some("other")
      ),
      costs = ContractCosts(
        minimumGuarantee = List(100, 200, 300),
        upfrontLicense = Some(50),
        technicalFee = Some(88),
        recoupable = Some(true)
      )
    ),
    List(createContractRestriction(id="client-facing", text = "madre mia")
    ))

    val json = VideoFormatter formatRow model.VideoTableRow(
      video = video,
      contract = Some(contract),
    )

    val contractJson = json.getAsJsonObject("contract")
    contractJson.getString("id") shouldBe "my-contract-id"
    contractJson.getString("name") shouldBe "my channel name"

    contractJson.getString("contractDocumentLink") shouldBe "http://mysite.com"
    contractJson.getBool("contractIsRolling") shouldBe false
    contractJson.getString("contractStartDate") shouldBe "2008-01-01"
    contractJson.getString("contractEndDate") shouldBe "2021-12-26"
    contractJson.getInt("daysBeforeTerminationWarning") shouldBe 300
    contractJson.getInt("yearsForMaximumLicense") shouldBe 1
    contractJson.getInt("daysForSellOffPeriod") shouldBe 101
    contractJson.getFloat("downloadRoyaltySplit") shouldBe 18.1F
    contractJson.getFloat("streamingRoyaltySplit") shouldBe 90F
    contractJson.getString("minimumPriceDescription") shouldBe "minimum price"
    contractJson.getString("remittanceCurrency") shouldBe "USD"

    contractJson.getStringList("clientFacingRestrictions") should contain("madre mia")
    contractJson.getString("territoryRestrictions") shouldBe "territory"
    contractJson.getString("licensingRestrictions") shouldBe "licensing"
    contractJson.getString("editingRestrictions") shouldBe "editing"
    contractJson.getString("marketingRestrictions") shouldBe "marketing"
    contractJson.getString("companiesRestrictions") shouldBe "companies"
    contractJson.getString("payoutRestrictions") shouldBe "payout"
    contractJson.getString("otherRestrictions") shouldBe "other"

    val minimumGuaranteesJson = contractJson.getAsJsonArray("minimumGuarantee")
    minimumGuaranteesJson.asScala.map(_.getAsJsonObject.getInt("contractYear")) shouldBe List(1, 2, 3)
    minimumGuaranteesJson.asScala.map(_.getAsJsonObject.getBigDecimal("amount")) shouldBe List(100, 200, 300)

    contractJson.getBigDecimal("upfrontLicenseCost") shouldBe 50
    contractJson.getBigDecimal("technicalFee") shouldBe 88
    contractJson.getBool("recoupable") shouldBe true
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

    val json = VideoFormatter formatRow model.VideoTableRow(
      video = video,
      impressions = List(impression)
    )

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
      userIdentity = createBoclipsUserIdentity("user-id"),
      subtype = Some("COOL-EVENT"),

    )

    val json = VideoFormatter formatRow model.VideoTableRow(
      video = video,
      interactions = List(interaction)
    )

    val interactionsJson = json.get("interactions").getAsJsonArray.get(0).getAsJsonObject
    interactionsJson.getString("timestamp") shouldBe "2020-10-20T10:11:12Z"
    interactionsJson.getString("subtype") shouldBe "COOL-EVENT"
    interactionsJson.getString("query") shouldBe "maths"
    interactionsJson.getString("id") should not be empty
  }

  it should "write topics" in {
    val video = createVideo(
      topics = List(
        VideoTopic(
          name = "1-1",
          confidence = 1.1,
          language = Locale.ENGLISH,
          parent =
            Some(
              VideoTopic(
                name = "1-2",
                confidence = 1.2,
                language = Locale.FRENCH,
                parent =
                  Some(
                    VideoTopic(
                      name = "1-3",
                      confidence = 1.3,
                      language = Locale.ITALIAN,
                      parent =
                        Some(
                          VideoTopic(
                            name = "1-4",
                            confidence = 1.4,
                            language = Locale.JAPAN,
                            parent =
                              Some(
                                VideoTopic(
                                  name = "1-5",
                                  confidence = 1.5,
                                  language = Locale.GERMANY
                                )
                              )
                          )
                        )
                    )
                  )
              )
            )
        ),
        VideoTopic(
          name = "2-1",
          confidence = 2.1,
          language = Locale.TRADITIONAL_CHINESE
        )
      )
    )

    val json = VideoFormatter formatRow model.VideoTableRow(video)

    val topicsArray = json.get("topics").getAsJsonArray
    topicsArray should have size 2

    val firstTopicGroup = topicsArray.get(0).getAsJsonObject
    val firstBaseTopic = firstTopicGroup.getAsJsonObject("topic")
    val firstParentTopic = firstTopicGroup.getAsJsonObject("parentTopic")
    val firstGrandparentTopic = firstTopicGroup.getAsJsonObject("grandparentTopic")

    val secondTopicGroup = topicsArray.get(1).getAsJsonObject
    val secondBaseTopic = secondTopicGroup.getAsJsonObject("topic")
    val secondParentTopic = secondTopicGroup.get("parentTopic")
    val secondGrandparentTopic = secondTopicGroup.get("grandparentTopic")

    firstBaseTopic.getString("name") shouldBe "1-1"
    firstBaseTopic.getDouble("confidence") shouldBe 1.1
    firstBaseTopic.getString("language") shouldBe "en"

    firstParentTopic.getString("name") shouldBe "1-2"
    firstParentTopic.getDouble("confidence") shouldBe 1.2
    firstParentTopic.getString("language") shouldBe "fr"

    firstGrandparentTopic.getString("name") shouldBe "1-3"
    firstGrandparentTopic.getDouble("confidence") shouldBe 1.3
    firstGrandparentTopic.getString("language") shouldBe "it"

    secondBaseTopic.getString("name") shouldBe "2-1"
    secondBaseTopic.getDouble("confidence") shouldBe 2.1
    secondBaseTopic.getString("language") shouldBe "zh-TW"

    secondParentTopic shouldBe null
    secondGrandparentTopic shouldBe null
  }

  it should "write youtube stats" in {
    val video = createVideo(id = "1")
    val stats = YouTubeVideoStats(
      videoId = VideoId("1"),
      viewCount = 120555
    )

    val json = VideoFormatter formatRow model.VideoTableRow(
      video,
      youTubeStats = Some(stats)
    )

    val statsObject = json.getAsJsonObject("youTubeStats")
    statsObject.getInt("viewCount") shouldBe 120555
  }
}
