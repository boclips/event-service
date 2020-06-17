package com.boclips.event.aggregator.presentation

import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent
import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.contentpartners.{Channel, Contract}
import com.boclips.event.aggregator.domain.model.orders.VideoItemWithOrder
import com.boclips.event.aggregator.domain.model.playbacks.PlaybackWithRelatedData
import com.boclips.event.aggregator.domain.model.search.VideoSearchResultImpression
import com.boclips.event.aggregator.domain.model.videos.Video

case class VideoWithRelatedData(
                                 video: Video,
                                 playbacks: List[PlaybackWithRelatedData] = Nil,
                                 orders: List[VideoItemWithOrder] = Nil,
                                 channel: Option[Channel] = None,
                                 contract: Option[Contract] = None,
                                 impressions: List[VideoSearchResultImpression] = Nil,
                                 interactions: List[VideoInteractedWithEvent] = Nil
                               )
