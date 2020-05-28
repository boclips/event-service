package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.events.EventConstants
import com.boclips.event.aggregator.domain.model.{AnonymousUser, Playback, User}
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.{JsonNull, JsonObject}

object PlaybackFormatter extends SingleRowFormatter[Playback] {

  def writeRow(playback: Playback, json: JsonObject): Unit = {

    val durationSeconds = playback.videoDuration.getSeconds.toInt
    val percentageWatched = durationSeconds match {
      case 0 => 0
      case _ => playback.secondsWatched.toDouble / durationSeconds
    }
    val url = playback.url

    val userId = playback.user match {
      case u: User => u.id
      case _: AnonymousUser => EventConstants.anonymousUserId
    }

    json.addDateTimeProperty("timestamp", playback.timestamp)
    json.addProperty("userId", userId.value)
    json.addProperty("videoId", playback.videoId.value)
    json.addProperty("secondsWatched", playback.secondsWatched)
    json.addProperty("percentageOfVideoDurationWatched", percentageWatched)
    json.addProperty("refererId", playback.refererId.map(_.value).orNull)
    json.addProperty("id", playback.id)
    json.addProperty("playbackId", playback.id)
    json.addProperty("durationSeconds", durationSeconds)
    json.addProperty("urlPath", url.map(_.path))
    json.addProperty("urlHost", url.map(_.host))
    json.addProperty("isShare", playback.isShare)
    json.addProperty("isPayable", playback.isPayable)
    json.addProperty("deviceId", playback.deviceId.map(_.value))

    playback.user match {
      case u: User => json.add("user", SimpleUserFormatter.formatRow(u))
      case _: AnonymousUser => json.add("user", JsonNull.INSTANCE)
    }
  }
}
