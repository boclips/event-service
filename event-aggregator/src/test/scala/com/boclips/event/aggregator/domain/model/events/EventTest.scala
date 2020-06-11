package com.boclips.event.aggregator.domain.model.events

import com.boclips.event.aggregator.domain.model.{DeviceId, UserId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory

class EventTest extends Test {

  "uniqueUserOrDeviceId" should "return user id when not anonymous" in {
    val event = EventFactory.createVideoSegmentPlayedEvent(userId = Some("bob"))

    val uniqueId = Event.uniqueUserOrDeviceId(event)

    uniqueId shouldBe UserId("bob")
  }

  it should "return device id when user id is not known" in {
    val event = EventFactory.createVideoSegmentPlayedEvent(
      userId = None,
      playbackDevice = Some("device")
    )

    val uniqueId = Event.uniqueUserOrDeviceId(event)

    uniqueId shouldBe DeviceId("device")
  }

  it should "fall back to anonymous user id when user is anonymous and no playback device" in {
    val event = EventFactory.createVideoSegmentPlayedEvent(
      userId = None,
      playbackDevice = None
    )

    val uniqueId = Event.uniqueUserOrDeviceId(event)

    uniqueId shouldBe DeviceId("UNKNOWN")
  }

}
