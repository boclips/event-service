package com.boclips.event.aggregator.presentation.assemblers

import com.boclips.event.aggregator.domain.model.collections.Collection
import com.boclips.event.aggregator.domain.model.contentpartners.{Channel, Contract}
import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent
import com.boclips.event.aggregator.domain.model.orders.{Order, VideoItemWithOrder}
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.search.VideoSearchResultImpression
import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.domain.model.videos.{Video, VideoId, YouTubeVideoStats}
import com.boclips.event.aggregator.presentation.model.{ContractTableRow, VideoTableRow}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object VideoTableRowAssembler {

  def assembleVideosWithRelatedData(
                                     videos: RDD[Video],
                                     playbacks: RDD[Playback],
                                     users: RDD[User],
                                     orders: RDD[Order],
                                     channels: RDD[Channel],
                                     contracts: RDD[ContractTableRow],
                                     collections: RDD[Collection],
                                     impressions: RDD[VideoSearchResultImpression],
                                     interactions: RDD[VideoInteractedWithEvent],
                                     youTubeVideoStats: RDD[YouTubeVideoStats],
                                   ): RDD[VideoTableRow] = {
    val playbacksByVideoId: RDD[(VideoId, Iterable[(Playback, Option[User])])] = (
      playbacks.keyBy(_.user) leftOuterJoin users.keyBy(_.identity)
      )
      .values
      .keyBy { case (playback, _) => playback.videoId }
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
      .join(contracts.keyBy(_.contract.id.value))
      .values
      .map { case ((videoId: VideoId, _), contract: ContractTableRow) => (videoId, contract) }

    val collectionListsByVideoId: RDD[(VideoId, Iterable[Collection])] = collections
      .flatMap(collection => collection.videoIds.map(videoId => (videoId, collection)))
      .groupBy(_._1)
      .map(it => (it._1, it._2.map(videoCollectionPairs => videoCollectionPairs._2)))

    val impressionsByVideoId: RDD[(VideoId, Iterable[VideoSearchResultImpression])] = impressions.keyBy(_.videoId)
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Search impressions by video ID")

    val interactionsByVideoId: RDD[(VideoId, Iterable[VideoInteractedWithEvent])] = interactions.keyBy(_.videoId)
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Interaction Events by video ID")

    val youTubeStatsByVideoId: RDD[(VideoId, YouTubeVideoStats)] =
      youTubeVideoStats.keyBy(_.videoId)

    videos
      .keyBy(_.id)
      .leftOuterJoin(playbacksByVideoId)
      .leftOuterJoin(orderItemsByVideoId)
      .leftOuterJoin(channelsByVideoId)
      .leftOuterJoin(contractsByVideoId)
      .leftOuterJoin(collectionListsByVideoId)
      .leftOuterJoin(impressionsByVideoId)
      .leftOuterJoin(interactionsByVideoId)
      .leftOuterJoin(youTubeStatsByVideoId)
      .values
      .map {
        case
          ((((((((
            video
            , videoPlaybacks
            ), videoOrders
            ), videoChannel
            ), videoContract
            ), collections
            ), videoImpressions
            ), videoInteractions
            ), youTubeStats
            ) =>
          VideoTableRow(
            video = video,
            youTubeStats = youTubeStats,
            playbacks = videoPlaybacks,
            orders = videoOrders,
            channel = videoChannel,
            contract = videoContract,
            collections = collections,
            impressions = videoImpressions,
            interactions = videoInteractions,
          )
      }
      .setName("Videos with related data")
      .persist(StorageLevel.DISK_ONLY)
  }
}
