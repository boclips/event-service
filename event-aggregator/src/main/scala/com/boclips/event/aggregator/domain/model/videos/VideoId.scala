package com.boclips.event.aggregator.domain.model.videos

case class VideoId(value: String) extends Ordered[VideoId] {

  override def compare(that: VideoId): Int = value.compare(that.value)
}
