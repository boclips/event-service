package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.domain.model.okrs.ActiveUserCounts
import com.boclips.event.aggregator.testsupport.Test

class ActiveUserCountsTest extends Test {

  "totalUsers" should "calculate the total number of users" in {
    ActiveUserCounts(newUsers = 100, repeatUsers = 200).totalUsers shouldBe 300
  }

  "plus" should "add all components" in {
    val sum = ActiveUserCounts(newUsers = 10, repeatUsers = 20) + ActiveUserCounts(newUsers = 40, repeatUsers = 50)
    sum.newUsers shouldBe 50
    sum.repeatUsers shouldBe 70
  }

}
