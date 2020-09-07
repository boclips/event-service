package com.boclips.event.aggregator.domain.model.videos

import java.util.Locale

case class VideoTopic(
                       name: String,
                       confidence: Double,
                       language: Locale,
                       parent: Option[VideoTopic] = None
                     )
