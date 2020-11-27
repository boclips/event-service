package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.users.{AnonymousUserIdentity, BoclipsUserIdentity, ExternalUserIdentity, User}
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.{JsonElement, JsonNull, JsonObject}

object SimplePlaybackFormatter extends SingleRowFormatter[Playback] {

  def writeRow(playback: Playback, json: JsonObject): Unit = {

    val durationSeconds = playback.videoDuration.getSeconds.toInt
    val percentageWatched = durationSeconds match {
      case 0 => 0
      case _ => playback.secondsWatched.toDouble / durationSeconds
    }
    val url = playback.url

    val (userId, identity) = playback.user match {
      case BoclipsUserIdentity(id) => (id.value, "BOCLIPS")
      case ExternalUserIdentity(id, externalId) => (s"${id.value}/${externalId.value}", "EXTERNAL")
      case AnonymousUserIdentity(Some(deviceId)) => (s"device:${deviceId.value}", "ANONYMOUS")
      case AnonymousUserIdentity(None) => throw new IllegalArgumentException()
    }

    json.addDateTimeProperty("timestamp", playback.timestamp)
    json.addProperty("userId", userId)
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
  }
}

object PlaybackFormatter extends SingleRowFormatter[(Playback, Option[User])] {
  override def writeRow(playbackWithUser: (Playback, Option[User]), json: JsonObject): Unit = {
    val (playback, userOption) = playbackWithUser
    SimplePlaybackFormatter.writeRow(playback, json)

    val userJson: JsonElement = userOption match {
      case Some(user) =>
        val userJsonObject = new JsonObject
        SimpleUserFormatter.writeRow(user, userJsonObject)
        userJsonObject
      case None => JsonNull.INSTANCE
    }
    json.add("user", userJson)
  }
}
