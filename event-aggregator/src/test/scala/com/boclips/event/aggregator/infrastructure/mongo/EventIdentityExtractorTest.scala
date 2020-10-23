package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.users.{AnonymousUserIdentity, BoclipsUserIdentity, DeviceId, ExternalUserId, ExternalUserIdentity, UserId}
import com.boclips.event.aggregator.infrastructure.mongo.EventIdentityExtractor.toEventDocumentWithIdentity
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.EventFactory

class EventIdentityExtractorTest extends Test {

  it should "convert user identity when boclips id specified and no external id" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = Some("the user"),
      externalUserId = None,
    )

    val event = toEventDocumentWithIdentity(  document, rdd())
    event.userIdentity shouldBe BoclipsUserIdentity(UserId("the user"))
  }

  it should "override userId when externalUserId is present and ExternalUserId exists in AllUsers" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = Some("the service account user"),
      externalUserId = Some("lti-user-1"),
    )

    val allUsers = Set("lti-user-1","the service account user")
    val event = toEventDocumentWithIdentity(document, allUsers)

    event.userIdentity shouldBe BoclipsUserIdentity(UserId("lti-user-1"))
  }


  it should "convert user identity when both boclips id and external id specified" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = Some("the user"),
      externalUserId = Some("pearson-user-1"),
    )

    val event = toEventDocumentWithIdentity(document, Set())

    event.userIdentity shouldBe ExternalUserIdentity(UserId("the user"), ExternalUserId("pearson-user-1"))
  }


  it should "create anonymous user identity for events with device id" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = None,
      externalUserId = None,
      deviceId = Some("device"),
    )

    val event = toEventDocumentWithIdentity(document,Set())

    event.userIdentity shouldBe AnonymousUserIdentity(Some(DeviceId("device")))
  }

  it should "create anonymous user identity for events without device id" in {
    val document = EventFactory.createVideoSegmentPlayedEventDocument(
      userId = None,
      externalUserId = None,
      deviceId = None,
    )

    val event = toEventDocumentWithIdentity(document,Set())

    event.userIdentity shouldBe AnonymousUserIdentity(None)
  }


}
