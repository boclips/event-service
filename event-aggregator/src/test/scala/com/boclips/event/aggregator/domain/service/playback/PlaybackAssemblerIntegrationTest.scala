package com.boclips.event.aggregator.domain.service.playback

import java.time.Duration

import com.boclips.event.aggregator.domain.model.sessions.Session
import com.boclips.event.aggregator.domain.model.videos.Video
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoSegmentPlayedEvent
import com.boclips.event.aggregator.testsupport.testfactories.SessionFactory.createSession
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideo
import org.apache.spark.rdd.RDD

class PlaybackAssemblerIntegrationTest extends IntegrationTest {

  it should "assemble playbacks from events" in sparkTest { implicit spark =>
    val sessions: RDD[Session] = rdd(
      createSession(events = List(
        createVideoSegmentPlayedEvent(videoId = "v1"),
        createVideoSegmentPlayedEvent(videoId = "v1"),
        createVideoSegmentPlayedEvent(videoId = "v2"),
      )),
      createSession(events = List(
        createVideoSegmentPlayedEvent(videoId = "v1"),
      ))
    )

    val videos: RDD[Video] = rdd(
      createVideo(id = "v1", duration = Duration.ofMinutes(1)),
      createVideo(id = "v2", duration = Duration.ofMinutes(1)),
    )

    val aggregator = new PlaybackAssembler(sessions, videos)

    val playbacks = aggregator.assemblePlaybacks().collect

    playbacks should have length 3
    playbacks.head.videoDuration shouldBe Duration.ofMinutes(1)
  }

}
