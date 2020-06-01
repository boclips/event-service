package com.boclips.event.aggregator.domain.service.playback

import com.boclips.event.aggregator.domain.model.events.VideoSegmentPlayedEvent
import com.boclips.event.aggregator.domain.model.{Playback, Session, Video, VideoId}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

class PlaybackAssembler(sessions: RDD[Session], videos: RDD[Video]) {

  def assemblePlaybacks(): RDD[Playback] = {

    def videosPlayedInSession(session: Session): Set[VideoId] = session.events.flatMap {
      case e: VideoSegmentPlayedEvent => Some(e.videoId)
      case _ => None
    }.toSet

    val idsOfVideosEverPlayed: RDD[VideoId] = sessions.flatMap(videosPlayedInSession)
      .distinct()
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Videos ever played")

    val durationSecondsByVideoId: Map[String, Int] = videos
      .keyBy(_.id)
      .join(idsOfVideosEverPlayed.keyBy(id => id))
      .values
      .keys
      .map(video => (video.id.value, video.duration.getSeconds.toInt))
      .collectAsMap()
      .toMap

    sessions
      .flatMap { sessionEvents => new SessionPlaybackAssembler().assemblePlaybacksInSession(sessionEvents, durationSecondsByVideoId) }
      .repartition(256)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Playbacks")
  }

}
