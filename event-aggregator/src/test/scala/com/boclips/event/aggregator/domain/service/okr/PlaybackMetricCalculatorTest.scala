package com.boclips.event.aggregator.domain.service.okr

import com.boclips.event.aggregator.domain.model.Monthly
import com.boclips.event.aggregator.domain.model.events.VideoSegmentPlayedEvent
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.{EventFactory, UserFactory}
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory.createVideoSegmentPlayedEvent
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity
import org.apache.spark.rdd.RDD

class PlaybackMetricCalculatorTest extends IntegrationTest {

  "calculateMetrics" should "calculate total and median of seconds watched" in sparkTest { implicit spark =>
    implicit val events: RDD[VideoSegmentPlayedEvent] = rdd(
      createVideoSegmentPlayedEvent(userIdentity = createBoclipsUserIdentity("aly"), secondsWatched = 30),
      createVideoSegmentPlayedEvent(userIdentity = createBoclipsUserIdentity("aly"), secondsWatched = 20),
      createVideoSegmentPlayedEvent(userIdentity = createBoclipsUserIdentity("ben"), secondsWatched = 5),
      createVideoSegmentPlayedEvent(userIdentity = createBoclipsUserIdentity("cal"), secondsWatched = 5)
    )

    val metrics = PlaybackMetricCalculator calculateMetrics Monthly()

    metrics should have size 1
    metrics.head.totalSecondsWatched shouldBe 60
    metrics.head.medianSecondsWatched shouldBe 5
  }
}
