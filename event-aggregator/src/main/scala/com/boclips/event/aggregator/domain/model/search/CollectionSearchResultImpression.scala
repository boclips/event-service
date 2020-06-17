package com.boclips.event.aggregator.domain.model.search

import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.CollectionSearchResultImpressionFormatter

case class CollectionSearchResultImpression(collectionId: CollectionId, search: SearchRequest, interaction: Boolean)

object CollectionSearchResultImpression {
  implicit val formatter: RowFormatter[CollectionSearchResultImpression] = CollectionSearchResultImpressionFormatter
}

