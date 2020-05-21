package com.boclips.event.aggregator.presentation.formatters

import java.time.Month.AUGUST

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.TestTimestamps.wholeMonthOf
import com.boclips.event.aggregator.testsupport.testfactories.SearchFactory.createQueryScore

class QueryScoreFormatterTest extends Test {

  it should "format json" in {
    val json = QueryScoreFormatter formatRow createQueryScore(
      timePeriod = wholeMonthOf(AUGUST, 2019),
      query = "what is math",
      score = 0.8,
      count = 10,
      hits = 6
    )

    json.get("start").getAsString shouldBe "2019-08-01T00:00:00Z"
    json.get("end").getAsString shouldBe "2019-09-01T00:00:00Z"
    json.get("month").getAsString shouldBe "2019-08"
    json.get("query").getAsString shouldBe "what is math"
    json.get("score").getAsDouble shouldBe 0.8
    json.get("count").getAsInt shouldBe 10
    json.get("hits").getAsInt shouldBe 6
  }
}
