package com.boclips.event.aggregator.presentation.formatters

import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.util.UUID

import com.boclips.event.aggregator.domain.model.sessions.Session
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject


object SessionFormatter extends SingleRowFormatter[Session] {

  override def writeRow(obj: Session, json: JsonObject): Unit = {
    json.addProperty("id", UUID.randomUUID().toString)
    json.addProperty("start", obj.start.format(ISO_OFFSET_DATE_TIME))
    json.addProperty("end", obj.end.format(ISO_OFFSET_DATE_TIME)
    )
    val sessionEventsJson = obj.events.map(event => {
      val eventJson = new JsonObject
      eventJson.addProperty("id", UUID.randomUUID().toString)
      eventJson.addProperty("userId", event.userIdentity.id.map(_.value))
      eventJson.addProperty("timestamp", event.timestamp.format(ISO_OFFSET_DATE_TIME))
      eventJson.addProperty("typeName", event.typeName)
      eventJson.addProperty("subtype", event.subtype.getOrElse(""))
      eventJson.addProperty("urlHost", event.url.map(_.host))
      eventJson.addProperty("urlPath", event.url.map(_.path))
      eventJson.addProperty("urlParams", event.url.map(_.rawParams))

      eventJson
    })
    json.addJsonArrayProperty("events", sessionEventsJson)
  }
}
