package com.boclips.event.aggregator.utils

import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.utils.Collections._

class CollectionsTest extends Test {

  "Sequence groupByKey" should "group values by key" in {
    val grouped = Seq((1, "a"), (2, "b"), (1, "c")).groupByKey()

    grouped(1) should contain only("a", "c")
    grouped(2) should contain only "b"
  }

  "fullJoin" should "match values by key when 2 maps" in {
    val m1 = Map(1 -> "a", 2 -> "b")
    val m2 = Map(1 -> "c", 3 -> "d")

    val union = Collections.fullJoin(m1, m2)
    union(1)._1 shouldBe Some("a")
    union(1)._2 shouldBe Some("c")

    union(2)._1 shouldBe Some("b")
    union(2)._2 shouldBe None

    union(3)._1 shouldBe None
    union(3)._2 shouldBe Some("d")
  }

  "fullJoin" should "match values by key when 3 maps" in {
    val m1 = Map(1 -> "a", 2 -> "b")
    val m2 = Map(1 -> "c", 3 -> "d")
    val m3 = Map(1 -> "e", 4 -> "f")

    val union = Collections.fullJoin(m1, m2, m3)
    union(1)._1 shouldBe Some("a")
    union(1)._2 shouldBe Some("c")
    union(1)._3 shouldBe Some("e")

    union(2)._1 shouldBe Some("b")
    union(2)._2 shouldBe None
    union(2)._3 shouldBe None

    union(3)._1 shouldBe None
    union(3)._2 shouldBe Some("d")
    union(3)._3 shouldBe None

    union(4)._1 shouldBe None
    union(4)._2 shouldBe None
    union(4)._3 shouldBe Some("f")
  }

}
