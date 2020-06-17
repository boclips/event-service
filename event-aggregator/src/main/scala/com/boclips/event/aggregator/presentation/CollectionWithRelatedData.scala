package com.boclips.event.aggregator.presentation

import com.boclips.event.aggregator.domain.model.events.CollectionInteractedWithEvent
import com.boclips.event.aggregator.domain.model.collections.Collection
import com.boclips.event.aggregator.domain.model.search.CollectionSearchResultImpression

case class CollectionWithRelatedData(collection: Collection, impressions: List[CollectionSearchResultImpression], interactions: List[CollectionInteractedWithEvent])
