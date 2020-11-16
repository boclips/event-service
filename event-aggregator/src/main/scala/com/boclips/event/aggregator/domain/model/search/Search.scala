package com.boclips.event.aggregator.domain.model.search

import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.Url
import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.users.UserIdentity
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.domain.service.search.SearchResultPlayback
import com.boclips.event.aggregator.presentation.RowFormatter
import com.boclips.event.aggregator.presentation.formatters.SearchFormatter

case class SearchImpression(
                             videoId: VideoId,
                             interaction: Boolean
                           )

case class CollectionImpression(
                                 collectionId: CollectionId,
                                 interaction: Boolean,
                               )

case class Search
(
  request: SearchRequest,
  response: SearchResponse,
  interactions: SearchInteractions,
)

case class SearchRequest
(
  id: String,
  url: Option[Url],
  timestamp: ZonedDateTime,
  userIdentity: UserIdentity,
  query: Query,
  queryParams: Map[String, Iterable[String]]
)

case class SearchResponse
(
  videoResults: Set[SearchImpression],
  collectionResults: Set[CollectionImpression],
  totalResults: Int,
  minResults: Int,
)

case class SearchInteractions
(
  videosPlayed: Iterable[SearchResultPlayback],
  resultPagesSeen: Int,
)

object Search {
  implicit val formatter: RowFormatter[Search] = SearchFormatter
}

case class Query(value: String) {

  def normalized(): String = value
    .toLowerCase
    .replaceAll("[^a-z0-9]", " ")
    .split("[\\s]+").map(_.trim).filter(_.nonEmpty).mkString(" ")
}
