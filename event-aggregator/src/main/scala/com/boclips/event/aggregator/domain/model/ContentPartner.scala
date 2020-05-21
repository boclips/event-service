package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.ContentPartnerFormatter

case class ContentPartner(
                           name: String,
                           playbackProvider: String
                         )

object ContentPartner {
  implicit val formatter: RowFormatter[ContentPartner] = ContentPartnerFormatter
}
