package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.testsupport.Test

class UserIdentityTest extends Test {

  "BoclipsUserIdentity" should "return user id as most specific identifier" in {
    val identity = BoclipsUserIdentity(
      id = UserId("user-id"),
      deviceId = Some(DeviceId("device-id")),
    )

    identity.mostSpecificIdentifier shouldBe UserId("user-id")
  }

  "AnonymousUserIdentity" should "return device id as most specific identifier" in {
    val identity = AnonymousUserIdentity(
      deviceId = Some(DeviceId("device-id")),
    )

    identity.mostSpecificIdentifier shouldBe DeviceId("device-id")
  }

  it should "return a fallback device id when as most specific identifier when device not known" in {
    val identity = AnonymousUserIdentity(None)

    identity.mostSpecificIdentifier shouldBe DeviceId("UNKNOWN DEVICE")
  }
}
