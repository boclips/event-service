package com.boclips.event.aggregator.domain.service.okr

import com.boclips.event.aggregator.domain.model.Monthly
import com.boclips.event.aggregator.domain.model.events.VideoSegmentPlayedEvent
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory
import org.apache.spark.rdd.RDD

class PlaybackMetricCalculatorTest extends IntegrationTest {

  "calculateMetrics" should "calculate total and median of seconds watched" in sparkTest { implicit spark =>
    implicit val events: RDD[VideoSegmentPlayedEvent] = rdd(
      EventFactory.createVideoSegmentPlayedEvent(userId = Some("aly"), secondsWatched = 30),
      EventFactory.createVideoSegmentPlayedEvent(userId = Some("aly"), secondsWatched = 20),
      EventFactory.createVideoSegmentPlayedEvent(userId = Some("ben"), secondsWatched = 5),
      EventFactory.createVideoSegmentPlayedEvent(userId = Some("cal"), secondsWatched = 5)
    )

    val metrics = PlaybackMetricCalculator calculateMetrics Monthly()

    metrics should have size 1
    metrics.head.totalSecondsWatched shouldBe 60
    metrics.head.medianSecondsWatched shouldBe 5
  }
}
