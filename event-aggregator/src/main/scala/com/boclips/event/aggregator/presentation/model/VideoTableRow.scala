package com.boclips.event.aggregator.presentation.model

import com.boclips.event.aggregator.domain.model.contentpartners.{Channel, Contract}
import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent
import com.boclips.event.aggregator.domain.model.orders.VideoItemWithOrder
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.search.VideoSearchResultImpression
import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.domain.model.videos.Video

case class VideoTableRow(
                                 video: Video,
                                 playbacks: List[(Playback, Option[User])] = Nil,
                                 orders: List[VideoItemWithOrder] = Nil,
                                 channel: Option[Channel] = None,
                                 contract: Option[Contract] = None,
                                 impressions: List[VideoSearchResultImpression] = Nil,
                                 interactions: List[VideoInteractedWithEvent] = Nil
                               )
