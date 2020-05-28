package com.boclips.event.aggregator.domain.service.video

import com.boclips.event.aggregator._
import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object VideoAssembler {

  def assembleVideosWithRelatedData(
                                     videos: RDD[Video],
                                     playbacks: RDD[Playback],
                                     orders: RDD[Order],
                                     channels: RDD[Channel],
                                     impressions: RDD[VideoSearchResultImpression],
                                     interactions: RDD[VideoInteractedWithEvent]
                                   ): RDD[VideoWithRelatedData] = {
    val playbacksByVideoId: RDD[(VideoId, Iterable[Playback])] = playbacks.keyBy(_.videoId)
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Playbacks by video ID")

    val orderItemsByVideoId = orders.flatMap(order => order.items.map(item => (item.videoId, VideoItemWithOrder(item, order))))
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Order items by video ID")

    val channelsByVideoId =
      videos.keyBy(_.contentPartner)
        .join(channels.keyBy(_.name))
        .values
        .map { case (video, channel) => (video.id, channel) }

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
      .leftOuterJoin(impressionsByVideoId)
      .leftOuterJoin(interactionsByVideoId)
      .values
      .map {
        case (((((video, videoPlaybacks), videoOrders), videoChannel), videoImpressions), videoInteractions) =>
          VideoWithRelatedData(
            video = video,
            playbacks = videoPlaybacks,
            orders = videoOrders,
            channel = videoChannel,
            impressions = videoImpressions,
            interactions = videoInteractions,
          )
      }
      .setName("Videos with related data")
      .persist(StorageLevel.DISK_ONLY)
  }
}


