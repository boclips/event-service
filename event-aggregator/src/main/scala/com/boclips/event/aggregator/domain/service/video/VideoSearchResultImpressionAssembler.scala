package com.boclips.event.aggregator.domain.service.video

import com.boclips.event.aggregator.domain.model.{Search, VideoSearchResultImpression}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object VideoSearchResultImpressionAssembler {

  def apply(searches: RDD[Search]): RDD[VideoSearchResultImpression] = {
    searches
      .flatMap(search =>
        search.response.videoResults.map(result =>
          VideoSearchResultImpression(videoId = result.videoId, search = search.request, interaction = result.interaction)
        )
      )
      .repartition(256)
      .setName("Video search result impressions")
      .persist(StorageLevel.DISK_ONLY)
  }
}
