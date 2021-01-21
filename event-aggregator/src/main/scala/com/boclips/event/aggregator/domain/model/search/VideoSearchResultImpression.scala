package com.boclips.event.aggregator.domain.model.search

import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.VideoSearchResultImpressionFormatter

case class VideoSearchResultImpression(
                                        videoId: VideoId,
                                        search: SearchRequestWithoutParams,
                                        interaction: Boolean
                                      )

object VideoSearchResultImpression {
  implicit val formatter: RowFormatter[VideoSearchResultImpression] = VideoSearchResultImpressionFormatter
}
