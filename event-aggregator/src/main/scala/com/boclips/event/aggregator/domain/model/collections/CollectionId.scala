package com.boclips.event.aggregator.domain.model.collections

case class CollectionId(value: String) extends Ordered[CollectionId] {

  override def compare(that: CollectionId): Int = value.compare(that.value)
}
