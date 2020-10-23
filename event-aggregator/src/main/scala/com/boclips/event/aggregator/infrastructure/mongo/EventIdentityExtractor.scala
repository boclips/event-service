package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.aggregator.domain.model.users.{AnonymousUserIdentity, BoclipsUserIdentity, DeviceId, ExternalUserId, ExternalUserIdentity, UserId, UserIdentity}
import com.boclips.event.aggregator.infrastructure.model.EventDocumentWithIdentity
import com.boclips.event.infrastructure.EventFields
import com.boclips.event.infrastructure.EventFields.{DEVICE_ID, EXTERNAL_USER_ID, USER_ID}
import org.bson.Document

object EventIdentityExtractor {

  final val LEGACY_ANONYMOUS_USER_ID: String = "anonymousUser"

  def toEventDocumentWithIdentity(event: Document, allUserIds: Set[String]): EventDocumentWithIdentity = {
    val externalUserIdOption = Option(event.getString(EventFields.EXTERNAL_USER_ID))
    val externalUserExistsInBoclips = externalUserIdOption.isDefined && allUserIds.contains(externalUserIdOption.get)

    EventDocumentWithIdentity(eventDocument = event, userIdentity = extractUserIdentity(event, externalUserExistsInBoclips))
  }

  def extractUserIdentity(event: Document, externalUserExistsInBoclips: Boolean): UserIdentity = {
    val userIdOption = Option(event.getString(USER_ID))
      .filter(_ != LEGACY_ANONYMOUS_USER_ID)
      .map(UserId)
    val externalUserIdOption = Option(event.getString(EXTERNAL_USER_ID))
      .map(ExternalUserId)
    val deviceIdOption = Option(event.getString(DEVICE_ID))
      .map(DeviceId)

    (userIdOption, externalUserIdOption, deviceIdOption, externalUserExistsInBoclips) match {
      case (Some(_), Some(externalUserId), _, true) => BoclipsUserIdentity(boclipsId = UserId(externalUserId.value))
      case (Some(userId), None, _, _) => BoclipsUserIdentity(userId)
      case (Some(userId), Some(externalUserId), _, _) => ExternalUserIdentity(userId, externalUserId)
      case (_, _, deviceId, _) => AnonymousUserIdentity(deviceId)
    }
  }
}
