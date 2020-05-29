package com.boclips.event.aggregator.presentation.formatters

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.events.EventConstants
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.{EventFactory, SessionFactory, UserFactory}

class SessionFormatterTest extends Test {

  it should "write id start and end" in {
    val json = SessionFormatter formatRow SessionFactory.createSession(UserFactory.createUser(id = "id-666"),
      List(
        EventFactory.createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.of(2017, 3, 23, 18, 25, 41, 511000000, ZoneOffset.UTC)),
        EventFactory.createVideoSegmentPlayedEvent(timestamp = ZonedDateTime.of(2017, 3, 23, 18, 30, 41, 511000000, ZoneOffset.UTC)))
    )

    json.getString("start") shouldBe "2017-03-23T18:25:41.511Z"
    json.getString("end") shouldBe "2017-03-23T18:30:41.511Z"

  }

  it should "write session events as a nested array" in {
    val json = SessionFormatter formatRow SessionFactory.createSession(UserFactory.createUser(id = "id-667"),
      List(EventFactory.createVideoInteractedWithEvent(timestamp = ZonedDateTime.of(2017, 3, 23, 18, 25, 41, 511000000, ZoneOffset.UTC),
        subtype = Some("VIDEO_LINK_COPIED"))))
    json.getAsJsonArray("events") should have size 1
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("typeName").getAsString shouldBe EventConstants.VIDEO_INTERACTED_WITH
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("subtype").getAsString shouldBe "VIDEO_LINK_COPIED"
  }

  it should "handle events with none subtypes" in {
    val json = SessionFormatter formatRow SessionFactory.createSession(UserFactory.createUser(id = "id-667"),
      List(EventFactory.createVideosSearchedEvent(timestamp = ZonedDateTime.of(2017, 3, 23, 18, 25, 41, 511000000, ZoneOffset.UTC))))
    json.getAsJsonArray("events") should have size 1
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("typeName").getAsString shouldBe EventConstants.VIDEOS_SEARCHED
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("subtype").getAsString shouldBe ""
  }

  it should "write id, userID, url host, path and params" in {
    val json = SessionFormatter formatRow SessionFactory.createSession(UserFactory.createUser(),
      List(EventFactory.createPageRenderedEvent(userId = "id-666", url = "https://teachers.boclips.com/videos?age_range=9-11&page=1&q=unit%20test"))
    )
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("id").getAsString should not be empty
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("urlHost").getAsString shouldBe "teachers.boclips.com"
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("urlPath").getAsString shouldBe "/videos"
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("urlParams").getAsString shouldBe "age_range=9-11&page=1&q=unit%20test"
    json.getAsJsonArray("events").get(0).getAsJsonObject.get("userId").getAsString shouldBe "id-666"
  }
}
