package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.ContentPartner
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object ContentPartnerFormatter extends SingleRowFormatter[ContentPartner] {

  override def writeRow(contentPartner: ContentPartner, json: JsonObject): Unit = {
    json.addProperty("name", contentPartner.name)
    json.addProperty("playbackProvider", contentPartner.playbackProvider)
  }
}
