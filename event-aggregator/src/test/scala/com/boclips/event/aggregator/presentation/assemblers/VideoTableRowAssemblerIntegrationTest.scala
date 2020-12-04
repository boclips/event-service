package com.boclips.event.aggregator.presentation.assemblers

import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.contentpackages.{ContentPackage, ContentPackageId}
import com.boclips.event.aggregator.domain.model.contentpartners.{ChannelDetails, ChannelId, ContractId}
import com.boclips.event.aggregator.domain.model.orders.OrderId
import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.domain.model.videos.{VideoId, YouTubeVideoStats}
import com.boclips.event.aggregator.presentation.formatters.schema.base.ExampleInstance
import com.boclips.event.aggregator.presentation.model.ContractTableRow
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.createChannel
import com.boclips.event.aggregator.testsupport.testfactories.CollectionFactory.createCollection
import com.boclips.event.aggregator.testsupport.testfactories.ContractFactory.createFullContract
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoInteractedWithEvent
import com.boclips.event.aggregator.testsupport.testfactories.OrderFactory.{createOrder, createOrderItem}
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.createPlayback
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.createVideoSearchResultImpression
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideo

class VideoTableRowAssemblerIntegrationTest extends IntegrationTest {

  it should "include relevant order items in videos" in sparkTest { implicit spark =>
    val videos = rdd(
      createVideo(id = "v1", channelId = "channel-1"),
      createVideo(id = "v2", channelId = "channel-2"),
    )

    val playbacks = rdd(
      createPlayback(videoId = "v1"),
      createPlayback(videoId = "v1"),
      createPlayback(videoId = "v2"),
    )

    val users = rdd[User]()

    val orders = rdd(
      createOrder(id = OrderId("o1"), items = List(
        createOrderItem(videoId = VideoId("v1"), priceGbp = BigDecimal(10)),
        createOrderItem(videoId = VideoId("v1"), priceGbp = BigDecimal(20)),
        createOrderItem(videoId = VideoId("v2")),
      ))
    )

    val basicChannelDetails = ExampleInstance.create[ChannelDetails]()
    val channels = rdd(
      createChannel(
        id = "channel-1",
        details = basicChannelDetails.copy(contractId = Some("contract-1"))
      ),
      createChannel(id = "channel-2"),
      createChannel(id = "unused-channel"),
    )

    val contracts = rdd(
      ContractTableRow(createFullContract(id = "contract-1"),clientFacingRestrictions = Nil),
      ContractTableRow(createFullContract(id = "unused-contract"),clientFacingRestrictions = Nil)
    )

    val collections = rdd(
      createCollection(
        id = "unused-collection-1"
      ),
      createCollection(
        id = "unused-collection-2",
        videoIds = List("nonexistent-id")
      ),
      createCollection(
        id = "collection-1",
        videoIds = List("v1", "v2")
      ),
      createCollection(
        id = "collection-2",
        videoIds = List("v1")
      )
    )

    val impressions = rdd(
      createVideoSearchResultImpression(videoId = VideoId("v1"), search = SearchFactory.createSearchRequest(query = "maths")),
      createVideoSearchResultImpression(videoId = VideoId("v1"), search = SearchFactory.createSearchRequest(query = "physics")),
    )

    val interactions = rdd(
      createVideoInteractedWithEvent(videoId = "v1"),
      createVideoInteractedWithEvent(videoId = "v1", subtype = Some("COOL-EVENT")),
      createVideoInteractedWithEvent(videoId = "v2"),
    )

    val youTubeStats = rdd(
      YouTubeVideoStats(
        videoId = VideoId("v1"), viewCount = 1111
      ),
      YouTubeVideoStats(
        videoId = VideoId("unused"), viewCount = 5555
      )
    )

    val contentPackages = rdd(
      ContentPackage(
        id = ContentPackageId("pack-1"),
        name = "pack-1"
      ),
      ContentPackage(
        id = ContentPackageId("pack-2"),
        name = "pack-2"
      ),
      ContentPackage(
        id = ContentPackageId("pack-3"),
        name = "pack-3"
      )
    )

    val videosForContentPackages = rdd(
      (ContentPackageId("pack-1"), VideoId("v1")),
      (ContentPackageId("pack-3"), VideoId("v1")),
    )

    val videosWithRelatedData = VideoTableRowAssembler.assembleVideosWithRelatedData(
      videos,
      playbacks,
      users,
      orders,
      channels,
      contracts,
      collections,
      impressions,
      interactions,
      youTubeStats,
      contentPackages,
      videosForContentPackages
    ).collect().toList.sortBy(_.video.id.value)

    videosWithRelatedData should have size 2
    videosWithRelatedData.head.video.id shouldBe VideoId("v1")
    videosWithRelatedData.head.youTubeStats.map(_.viewCount) should contain(1111)
    videosWithRelatedData.head.playbacks should have size 2
    videosWithRelatedData.head.orders should have size 2
    videosWithRelatedData.head.channel.map(_.id) should contain(ChannelId("channel-1"))
    videosWithRelatedData.head.contract.map(_.contract.id) should contain(ContractId("contract-1"))
    videosWithRelatedData.head.interactions should have size 2
    videosWithRelatedData.head.collections.map(_.id) shouldBe List(
      CollectionId("collection-1"),
      CollectionId("collection-2")
    )
    videosWithRelatedData.head.contentPackageNames shouldBe List(
      "pack-1",
      "pack-3"
    )

    val v1Orders = videosWithRelatedData.head.orders.sortBy(_.order.id.value)
    v1Orders.head.order.id shouldBe OrderId("o1")
    v1Orders.head.item.videoId shouldBe VideoId("v1")
    v1Orders.head.item.priceGbp shouldBe BigDecimal(10)

    videosWithRelatedData.head.impressions should have size 2
  }
}
