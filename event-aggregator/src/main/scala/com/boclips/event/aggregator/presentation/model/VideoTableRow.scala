package com.boclips.event.aggregator.presentation.model

import com.boclips.event.aggregator.domain.model.collections.Collection
import com.boclips.event.aggregator.domain.model.contentpackages.ContentPackage
import com.boclips.event.aggregator.domain.model.contentpartners.Channel
import com.boclips.event.aggregator.domain.model.events.VideoInteractedWithEvent
import com.boclips.event.aggregator.domain.model.orders.VideoItemWithOrder
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.search.VideoSearchResultImpression
import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.domain.model.videos.{Video, YouTubeVideoStats}

case class VideoTableRow(
                          video: Video,
                          youTubeStats: Option[YouTubeVideoStats] = None,
                          playbacks: List[(Playback, Option[User])] = Nil,
                          orders: List[VideoItemWithOrder] = Nil,
                          channel: Option[Channel] = None,
                          contract: Option[ContractTableRow] = None,
                          collections: List[Collection] = Nil,
                          impressions: List[VideoSearchResultImpression] = Nil,
                          interactions: List[VideoInteractedWithEvent] = Nil,
                          contentPackageNames: List[String] = Nil,
                        )
