package com.boclips.event.aggregator.domain.service.search

import com.boclips.event.aggregator.testsupport.Test

class BetaDistributionTest extends Test {

  "add a single result" should "increment alpha when it's a success" in {
    BetaDistribution(3, 5) add true shouldBe BetaDistribution(4, 5)
  }

  it should "increment beta when it's a failure" in {
    BetaDistribution(3, 5) add false shouldBe BetaDistribution(3, 6)
  }

  "add another distribution" should "sum both parameters" in {
    BetaDistribution(3, 5) add BetaDistribution(10, 11) shouldBe BetaDistribution(13, 16)
  }

  "mean" should "calculate the ratio" in {
    BetaDistribution(3, 7).mean() shouldBe 0.3
  }

  "total" should "sum alpha and beta" in {
    BetaDistribution(4, 6).total() shouldBe 10
  }
}
