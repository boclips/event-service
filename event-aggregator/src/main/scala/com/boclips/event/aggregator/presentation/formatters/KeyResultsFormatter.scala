package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.okrs.KeyResults
import com.boclips.event.aggregator.presentation.formatters.common.SingleRowFormatter
import com.google.gson.JsonObject

object KeyResultsFormatter extends SingleRowFormatter[KeyResults] {

  override def writeRow(keyResults: KeyResults, json: JsonObject): Unit = {
    json.addMonthProperty("month", keyResults.timePeriod.startInclusive)
    json.addDateTimeProperty("start", keyResults.timePeriod.startInclusive)
    json.addDateTimeProperty("end", keyResults.timePeriod.endExclusive)
    json.addProperty("activeUsersTotal", keyResults.userStats.activeUserCounts.totalUsers)
    json.addProperty("activeUsersNew", keyResults.userStats.activeUserCounts.newUsers)
    json.addProperty("activeUsersRepeat", keyResults.userStats.activeUserCounts.repeatUsers)
    json.addProperty("newAccounts", keyResults.userStats.newAccounts)
    json.addProperty("totalAccountsAtEnd", keyResults.userStats.totalsAtEnd.totalAccounts)
    json.addProperty("repeatRate", keyResults.userStats.repeatRate)
    json.addProperty("churnRate", formatChurnRate(keyResults.userStats.churnRate))

    json.addProperty("medianMinsWatched", keyResults.playbackMetric.medianSecondsWatched / 60.0)
    json.addProperty("totalHoursWatched", keyResults.playbackMetric.totalSecondsWatched / 3600)

    json.addProperty("percentageSearchesLeadingToPlayback", keyResults.searchStats.percentageSearchesLeadingToPlayback)
    json.addProperty("percentagePlaybacksTop3", keyResults.searchStats.percentagePlaybacksTop3)
  }

  private def formatChurnRate(churnRate: Double): Double = {
    if (Double.box(churnRate).isNaN) 0 else churnRate
  }
}
