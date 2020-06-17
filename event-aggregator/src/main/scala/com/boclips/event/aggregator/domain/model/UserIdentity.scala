package com.boclips.event.aggregator.domain.model

sealed trait UserIdentity {
  def id: Option[UserId]

  def deviceId: Option[DeviceId]
}

case class BoclipsUserIdentity(
                                boclipsId: UserId
                              ) extends UserIdentity {
  override def id: Option[UserId] = Some(boclipsId)

  override def deviceId: Option[DeviceId] = None

}

case class ExternalUserId(value: String)

case class ExternalUserIdentity(
                                boclipsId: UserId,
                                externalId: ExternalUserId,
                               ) extends UserIdentity {
  override def id: Option[UserId] = Some(boclipsId)

  override def deviceId: Option[DeviceId] = None
}

case class AnonymousUserIdentity(
                                  deviceId: Option[DeviceId]
                                ) extends UserIdentity {
  override def id: Option[UserId] = None
}
