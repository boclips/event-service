package com.boclips.event.aggregator.domain.service.playback

import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.domain.model.playbacks.{Playback, PlaybackWithRelatedData}
import org.apache.spark.rdd.RDD

class PlaybackWithRelatedDataAssembler(playbacks: RDD[Playback], users: RDD[User]) {

  def assemblePlaybacksWithRelatedData(): RDD[PlaybackWithRelatedData] = {
    val playbacksByUserId = playbacks.keyBy(playback => playback.user)
    val usersById = users.keyBy(_.identity)

    playbacksByUserId.leftOuterJoin(usersById).values.map {
      case (playback, user) => PlaybackWithRelatedData(playback, user)
    }
  }
}
