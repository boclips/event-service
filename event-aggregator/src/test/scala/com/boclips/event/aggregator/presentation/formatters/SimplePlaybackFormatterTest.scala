package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.PlaybackFactory.createPlayback
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.createExternalUserIdentity

class SimplePlaybackFormatterTest extends Test {
  it should "write external user id when user is external" in {
    val json = SimplePlaybackFormatter.formatRow(createPlayback(user = createExternalUserIdentity(
      boclipsId = "pearson-service-account-id",
      externalId = "mr-pearson-user-id"
    )))
    json.getString("userId") shouldBe "pearson-service-account-id/mr-pearson-user-id"
  }
}
