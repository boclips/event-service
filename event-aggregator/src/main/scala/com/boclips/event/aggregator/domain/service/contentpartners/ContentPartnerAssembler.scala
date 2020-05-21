package com.boclips.event.aggregator.domain.service.contentpartners

import com.boclips.event.aggregator.domain.model.{ContentPartner, Video}
import org.apache.spark.rdd.RDD

object ContentPartnerAssembler {

  def apply(videos: RDD[Video]): RDD[ContentPartner] = {
    videos
      .map(video => ContentPartner(name = video.contentPartner, playbackProvider = video.playbackProvider))
      .distinct()
  }
}

case class ContentPartnerId(name: String, playbackProvider: String)

object ContentPartnerId {
  def apply(video: Video): ContentPartnerId = {
    ContentPartnerId(video.contentPartner, playbackProvider = video.playbackProvider)
  }
}
