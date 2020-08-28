package com.boclips.event.aggregator.infrastructure.youtube

import com.boclips.event.aggregator.config.YouTubeConfig
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.testsupport.IntegrationTest

class YouTubeServiceIntegrationTest extends IntegrationTest {
  it should "get video statistics from YouTube" in {
    val videoIdsByPlaybackId = Map(
      ("9bZkp7q19f0", VideoId("gangnam style")),
      ("A02s8omM_hI", VideoId("sileny ota"))
    )

    val result = YouTubeService(
      YouTubeConfig.fromEnv
    ).getVideoStats(
      videoIdsByPlaybackId
    ).sortBy(_.viewCount)

    result.head.videoId shouldBe VideoId("sileny ota")
    result.drop(1).head.videoId shouldBe VideoId("gangnam style")
  }
}
