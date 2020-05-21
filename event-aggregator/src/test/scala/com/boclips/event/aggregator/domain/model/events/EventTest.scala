package com.boclips.event.aggregator.domain.model.events

import com.boclips.event.aggregator.domain.model.{DeviceId, UserId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory

class EventTest extends Test {

  "uniqueUserOrDeviceId" should "return user id when not anonymous" in {
    val uniqueId = Event.uniqueUserOrDeviceId(EventFactory.createVideoSegmentPlayedEvent(userId = "bob"))

    uniqueId shouldBe UserId("bob")
  }

  it should "return device id when user was anonymous" in {
    val uniqueId = Event.uniqueUserOrDeviceId(EventFactory.createVideoSegmentPlayedEvent(userId = EventConstants.anonymousUserId.value, playbackDevice = Some("device")))

    uniqueId shouldBe DeviceId("device")
  }

  it should "fall back to anonymous user id when user is anonymous and no playback device" in {
    val uniqueId = Event.uniqueUserOrDeviceId(EventFactory.createVideoSegmentPlayedEvent(userId = EventConstants.anonymousUserId.value, playbackDevice = None))

    uniqueId shouldBe DeviceId("UNKNOWN")
  }

}
