package com.boclips.event.aggregator.presentation.model

import com.boclips.event.aggregator.domain.model.collections.Collection
import com.boclips.event.aggregator.domain.model.events.CollectionInteractedWithEvent
import com.boclips.event.aggregator.domain.model.search.CollectionSearchResultImpression

case class CollectionTableRow(
                               collection: Collection,
                               impressions: List[CollectionSearchResultImpression],
                               interactions: List[CollectionInteractedWithEvent],
                             )
