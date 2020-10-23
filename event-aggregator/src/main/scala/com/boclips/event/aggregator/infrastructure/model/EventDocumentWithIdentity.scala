package com.boclips.event.aggregator.infrastructure.model

import com.boclips.event.aggregator.domain.model.users.UserIdentity
import org.bson.Document

case class EventDocumentWithIdentity(
                                      eventDocument: Document,
                                      userIdentity: UserIdentity
)
