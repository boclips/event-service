package com.boclips.event.aggregator.infrastructure.mongo

import com.boclips.event.infrastructure.EventFields
import org.bson.Document

object EventEnricher {

  def markExternalUserExists(event: Document, allUserIds: Set[String]): Document = {
    val externalUserIdOption = Option(event.getString(EventFields.EXTERNAL_USER_ID))
    val externalUserExists = externalUserIdOption.isDefined && allUserIds.contains(externalUserIdOption.get)

    event.append(EventFields.EXTERNAL_USER_EXISTS, externalUserExists)
    new Document()
  }
}
