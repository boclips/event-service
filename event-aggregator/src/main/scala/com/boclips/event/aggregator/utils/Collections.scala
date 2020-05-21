package com.boclips.event.aggregator.utils

object Collections {

  implicit class SeqExt[TKey, TValue](val self: Seq[(TKey, TValue)]) {
    def groupByKey(): Map[TKey, Iterable[TValue]] = self.groupBy(_._1).mapValues(values => values.map(_._2))
  }

  implicit class MapExt[TKey, TValue](val self: Map[TKey, TValue]) {
    def fullJoin[TOtherValue](other: Map[TKey, TOtherValue]): Map[TKey, (Option[TValue], Option[TOtherValue])] = {
      Collections.fullJoin(self, other)
    }
  }

  def fullJoin[TKey, TValue1, TValue2](map1: Map[TKey, TValue1], map2: Map[TKey, TValue2]): Map[TKey, (Option[TValue1], Option[TValue2])] = {
    Collections.fullJoin(map1, map2, Map[TKey, Nothing]()).mapValues {
      case (v1, v2, _) => (v1, v2)
    }
  }

  def fullJoin[TKey, TValue1, TValue2, TValue3](map1: Map[TKey, TValue1], map2: Map[TKey, TValue2], map3: Map[TKey, TValue3]): Map[TKey, (Option[TValue1], Option[TValue2], Option[TValue3])] = {
    (map1.keySet ++ map2.keySet ++ map3.keySet).map { key =>
      (key, (map1.get(key), map2.get(key), map3.get(key)))
    }
      .toMap
  }
}
