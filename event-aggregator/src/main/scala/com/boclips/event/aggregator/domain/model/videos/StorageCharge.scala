package com.boclips.event.aggregator.domain.model.videos

import java.time.LocalDate

case class VideoStorageCharge(videoId: VideoId, periodStart: LocalDate, periodEnd: LocalDate, valueGbp: Double) {

  def id = s"${videoId.value}-${periodEnd}"
}
