package com.boclips.event.aggregator.domain.model.search

case class SearchPhrase(phrase: String, playback: Long, noPlayback: Long) {
  def estimatePlaybackProbability(alpha: Double, beta: Double): Double = (alpha + playback) / (alpha + beta + count)

  def count: Long = playback + noPlayback
}
