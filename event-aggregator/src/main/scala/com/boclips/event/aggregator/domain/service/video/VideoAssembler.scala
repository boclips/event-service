package com.boclips.event.aggregator.domain.service.video

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object VideoAssembler {

  def assembleVideosWithRelatedData(
                                     videos: RDD[Video],
                                     playbacks: RDD[PlaybackWithRelatedData],
                                     orders: RDD[Order],
                                     channels: RDD[Channel],
                                     contracts: RDD[Contract],
                                     impressions: RDD[VideoSearchResultImpression],
                                     interactions: RDD[VideoInteractedWithEvent]
                                   ): RDD[VideoWithRelatedData] = {
    val playbacksByVideoId: RDD[(VideoId, Iterable[PlaybackWithRelatedData])] = playbacks.keyBy(_.playback.videoId)
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Playbacks by video ID")

    val orderItemsByVideoId = orders.flatMap(order => order.items.map(item => (item.videoId, VideoItemWithOrder(item, order))))
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Order items by video ID")

    val channelsByVideoId = videos
      .keyBy(_.channelId)
      .join(channels.keyBy(_.id))
      .values
      .map { case (video, channel) => (video.id, channel) }

    val contractsByVideoId = channelsByVideoId
      .keyBy(_._2.details.contractId)
      .flatMap { case (key, videoIdAndChannel) =>
        key match {
          case Some(contractId) => Seq((contractId, videoIdAndChannel))
          case _ => Nil
        }
      }
      .join(contracts.keyBy(_.id.value))
      .values
      .map { case ((videoId: VideoId, _), contract: Contract) => (videoId, contract) }

    val impressionsByVideoId: RDD[(VideoId, Iterable[VideoSearchResultImpression])] = impressions.keyBy(_.videoId)
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Search impressions by video ID")

    val interactionsByVideoId: RDD[(VideoId, Iterable[VideoInteractedWithEvent])] = interactions.keyBy(_.videoId)
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Interaction Events by video ID")

    videos.keyBy(_.id)
      .leftOuterJoin(playbacksByVideoId)
      .leftOuterJoin(orderItemsByVideoId)
      .leftOuterJoin(channelsByVideoId)
      .leftOuterJoin(contractsByVideoId)
      .leftOuterJoin(impressionsByVideoId)
      .leftOuterJoin(interactionsByVideoId)
      .values
      .map {
        case ((((((video, videoPlaybacks), videoOrders), videoChannel), videoContract), videoImpressions), videoInteractions) =>
          VideoWithRelatedData(
            video = video,
            playbacks = videoPlaybacks,
            orders = videoOrders,
            channel = videoChannel,
            contract = videoContract,
            impressions = videoImpressions,
            interactions = videoInteractions,
          )
      }
      .setName("Videos with related data")
      .persist(StorageLevel.DISK_ONLY)
  }
}


