package com.boclips.event.aggregator.domain.service.playback

import com.boclips.event.aggregator.domain.model.{Playback, PlaybackWithRelatedData, User, UserId}
import org.apache.spark.rdd.RDD

class PlaybackWithRelatedDataAssembler(playbacks: RDD[Playback], users: RDD[User]) {

  def assemblePlaybacksWithRelatedData(): RDD[PlaybackWithRelatedData] = {
    val playbacksByUserId = playbacks.keyBy(playback => playback.user.boclipsId.getOrElse(UserId("")))
    val usersById = users.keyBy(_.id)

    playbacksByUserId.leftOuterJoin(usersById).values.map {
      case (playback, user) => PlaybackWithRelatedData(playback, user)
    }
  }
}
