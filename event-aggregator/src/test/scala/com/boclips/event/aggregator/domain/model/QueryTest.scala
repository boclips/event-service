package com.boclips.event.aggregator.domain.model

import com.boclips.event.aggregator.testsupport.Test

class QueryTest extends Test {

  it should "lowercase the queries" in {
    Query("QUERY").normalized() shouldBe "query"
  }

  it should "remove multiple whitespaces" in {
    Query("  a   b    ").normalized() shouldBe "a b"
  }

  it should "remove non-alphanumeric characters" in {
    Query("a,b: c=d'e0").normalized() shouldBe "a b c d e0"
  }
}
