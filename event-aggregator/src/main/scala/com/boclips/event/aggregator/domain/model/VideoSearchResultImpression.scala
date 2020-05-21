package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.VideoSearchResultImpressionFormatter

case class VideoSearchResultImpression(videoId: VideoId, search: SearchRequest, interaction: Boolean)

object VideoSearchResultImpression {
  implicit val formatter: RowFormatter[VideoSearchResultImpression] = VideoSearchResultImpressionFormatter
}
