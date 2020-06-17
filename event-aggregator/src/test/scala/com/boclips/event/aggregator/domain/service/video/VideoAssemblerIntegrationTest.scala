package com.boclips.event.aggregator.domain.service.video

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.contentpartners.{ChannelDetails, ChannelId, ContractId}
import com.boclips.event.aggregator.domain.model.orders.OrderId
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.presentation.formatters.schema.base.ExampleInstance
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.ChannelFactory.createChannel
import com.boclips.event.aggregator.testsupport.testfactories.ContractFactory.createFullContract
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoInteractedWithEvent
import com.boclips.event.aggregator.testsupport.testfactories.OrderFactory.{createOrder, createOrderItem}
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.{createPlayback, createPlaybackWithRelatedData}
import com.boclips.event.aggregator.testsupport.testfactories.{PlaybackFactory, SearchFactory}
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.createVideoSearchResultImpression
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideo

class VideoAssemblerIntegrationTest extends IntegrationTest {

  it should "include relevant order items in videos" in sparkTest { implicit spark =>
    val videos = rdd(
      createVideo(id = "v1", channelId = "channel-1"),
      createVideo(id = "v2", channelId = "channel-2"),
    )

    val playbacks = rdd(
      createPlaybackWithRelatedData(createPlayback(videoId = "v1")),
      createPlaybackWithRelatedData(createPlayback(videoId = "v1")),
      createPlaybackWithRelatedData(createPlayback(videoId = "v2")),
    )

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
      createFullContract(id = "contract-1"),
      createFullContract(id = "unused-contract")
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

    val videosWithRelatedData = VideoAssembler.assembleVideosWithRelatedData(
      videos,
      playbacks,
      orders,
      channels,
      contracts,
      impressions,
      interactions
    ).collect().toList.sortBy(_.video.id.value)

    videosWithRelatedData should have size 2
    videosWithRelatedData.head.video.id shouldBe VideoId("v1")
    videosWithRelatedData.head.playbacks should have size 2
    videosWithRelatedData.head.orders should have size 2
    videosWithRelatedData.head.channel.map(_.id) should contain(ChannelId("channel-1"))
    videosWithRelatedData.head.contract.map(_.id) should contain(ContractId("contract-1"))
    videosWithRelatedData.head.interactions should have size 2

    val v1Orders = videosWithRelatedData.head.orders.sortBy(_.order.id.value)
    v1Orders.head.order.id shouldBe OrderId("o1")
    v1Orders.head.item.videoId shouldBe VideoId("v1")
    v1Orders.head.item.priceGbp shouldBe BigDecimal(10)

    videosWithRelatedData.head.impressions should have size 2
  }
}
