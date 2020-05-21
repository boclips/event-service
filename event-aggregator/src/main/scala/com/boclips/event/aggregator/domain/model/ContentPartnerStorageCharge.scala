package com.boclips.event.aggregator.domain.model

import java.time.LocalDate

case class ContentPartnerStorageCharge(contentPartner: String, periodStart: LocalDate, periodEnd: LocalDate, valueGbp: Double) {

  def id = s"${contentPartner}-${periodEnd}"
}
