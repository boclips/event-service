package com.boclips.event.aggregator.presentation.formatters

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{Url, VideoId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory._
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createBoclipsUserIdentity

class VideoSearchResultImpressionFormatterTest extends Test {
  it should "write timestamp" in {
    val impression = createVideoSearchResultImpression(search = createSearchRequest(timestamp = ZonedDateTime.of(2012, 4, 23, 18, 25, 43, 511000000, ZoneOffset.UTC)))

    val json = VideoSearchResultImpressionFormatter formatRow impression

    json.get("timestamp").getAsString shouldBe "2012-04-23T18:25:43.511Z"
  }

  it should "write impression interacted with" in {

    val impression = createVideoSearchResultImpression(interaction = true)

    val json = VideoSearchResultImpressionFormatter formatRow impression

    json.get("interaction").getAsBoolean shouldBe true

  }

  it should "write the normalized search query" in {

    val impression = createVideoSearchResultImpression(search = createSearchRequest(query = "Richard"))

    val json = VideoSearchResultImpressionFormatter formatRow impression

    json.get("query").getAsString shouldBe "richard"
  }

  it should "write user id" in {
    val impression = createVideoSearchResultImpression(search = createSearchRequest(userIdentity = createBoclipsUserIdentity("dave")))

    val json = VideoSearchResultImpressionFormatter formatRow impression

    json.get("userId").getAsString shouldBe "dave"
  }

  it should "write video id" in {
    val impression = createVideoSearchResultImpression(videoId = VideoId("video-id-1"))
    val json = VideoSearchResultImpressionFormatter formatRow impression
    json.get("videoId").getAsString shouldBe "video-id-1"
  }

  it should "write url host" in {
    val impression = createVideoSearchResultImpression(search = createSearchRequest(url = Some(Url.parse("https://boclips.com/bla"))))

    val json = VideoSearchResultImpressionFormatter formatRow impression

    json.getString("urlHost") shouldBe "boclips.com"
  }
}
