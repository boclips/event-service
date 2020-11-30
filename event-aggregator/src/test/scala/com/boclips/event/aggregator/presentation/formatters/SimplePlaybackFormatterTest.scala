package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.createPlayback
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createAnonymousUserIdentity, createExternalUserIdentity}

class SimplePlaybackFormatterTest extends Test {

  it should "write external user id when user is external" in {
    val json = SimplePlaybackFormatter.formatRow(createPlayback(user = createExternalUserIdentity(
      boclipsId = "pearson-service-account-id",
      externalId = "mr-pearson-user-id"
    )))
    json.getString("userId") shouldBe "pearson-service-account-id/mr-pearson-user-id"
  }

  it should "write unknown device id when Anonymous identity" in {
    val json = SimplePlaybackFormatter.formatRow(createPlayback(user = createAnonymousUserIdentity(
      deviceId = None
    )))
    json.getString("userId") shouldBe "UnknownDeviceId"
  }
}
