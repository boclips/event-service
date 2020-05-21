package com.boclips.event.aggregator.domain.model

import java.time.LocalDate

case class VideoStorageCharge(videoId: VideoId, contentPartner: String, periodStart: LocalDate, periodEnd: LocalDate, valueGbp: Double) {

  def id = s"${videoId.value}-${periodEnd}"
}
