package com.boclips.event.aggregator.domain.service.search

import java.util.UUID

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events._

import scala.reflect.ClassTag

case class SearchResultPlayback(videoId: VideoId, videoIndex: Option[Int], secondsPlayed: Int)

class SessionSearchAssembler() {

  def assembleSearchesInSession(implicit session: Session): Iterable[Search] = {
    val searchesByQuery = groupByQuery[VideosSearchedEvent](search => Some(search.query))
    val collectionSearchesByQuery = groupByQuery[CollectionSearchedEvent](collectionSearch => Some(collectionSearch.query))
    val playbacksByQuery = groupByQuery[VideoSegmentPlayedEvent](_.query)
    val interactionsByQuery = groupByQuery[VideoInteractedWithEvent](_.query)
    val collectionInteractionsByQuery = groupByQuery[CollectionInteractedWithEvent](_.query)
    val videoAddedToCollectionByQuery = groupByQuery[VideoAddedToCollectionEvent](_.query)

    searchesByQuery.map {
      case (query, searchEvents) => mergeEventsWhereQueryIsTheSame(
        searchEvents,
        collectionSearchesByQuery.getOrElse(query, List()),
        playbacksByQuery.getOrElse(query, List()),
        interactionsByQuery.getOrElse(query, List()),
        videoAddedToCollectionByQuery.getOrElse(query, List()),
        collectionInteractionsByQuery.getOrElse(query, List()),
      )
    }
  }

  private def mergeEventsWhereQueryIsTheSame(searchEvents: Iterable[VideosSearchedEvent],
                                             collectionSearchedEvents: Iterable[CollectionSearchedEvent],
                                             playbackEvents: Iterable[VideoSegmentPlayedEvent],
                                             videoInteractedWithEvents: Iterable[VideoInteractedWithEvent],
                                             videoAddedToCollectionEvent: Iterable[VideoAddedToCollectionEvent],
                                             collectionInteractedWithEvents: Iterable[CollectionInteractedWithEvent],
                                            ): Search = {
    val searchEvent :: _ = searchEvents
    val timestamp = searchEvents.map(_.timestamp).minBy(_.toEpochSecond)
    val pagesSeen = searchEvents.map(_.pageIndex).toSet.size
    val totalResults = searchEvents.map(_.totalResults).toSet.max
    val minResults = searchEvents.map(_.totalResults).toSet.min
    val urlParamsKeys = searchEvents.flatMap(_.url.map(_.params)).map(_.keys).flatten.toSet
    val videosWithInteractions: Set[VideoId] = playbackEvents.map(_.videoId).toSet ++ videoInteractedWithEvents.map(_.videoId) ++ videoAddedToCollectionEvent.map(_.videoId)
    val videoResults = searchEvents.flatMap(_.videoResults.getOrElse(List()))
      .map(videoId => SearchImpression(videoId = videoId, interaction = videosWithInteractions.contains(videoId)))
      .toSet

    val collectionsWithInteractions: Set[CollectionId] = collectionInteractedWithEvents.map(_.collectionId).toSet
    val collectionResults = collectionSearchedEvents
      .flatMap(_.collectionResults)
      .map(collectionId => CollectionImpression(collectionId = collectionId, interaction = collectionsWithInteractions.contains(collectionId)))
      .toSet

    val videosPlayed = playbackEvents.groupBy(event => (event.videoId, event.videoIndex)).map {
      case ((videoId, videoIndex), playbacks) => SearchResultPlayback(videoId, videoIndex, playbacks.map(_.secondsWatched).sum)
    }
    Search(
      request = SearchRequest(
        id = UUID.randomUUID().toString,
        timestamp = timestamp,
        userId = searchEvent.userIdPresent,
        query = searchEvent.query,
        url = searchEvent.url,
        urlParamsKeys = urlParamsKeys,
      ),
      response = SearchResponse(
        videoResults = videoResults,
        collectionResults = collectionResults,
        totalResults = totalResults,
        minResults = minResults,
      ),
      interactions = SearchInteractions(
        videosPlayed = videosPlayed,
        resultPagesSeen = pagesSeen,
      ),
    )
  }

  def groupByQuery[T](getQuery: T => Option[Query])(implicit session: Session, tag: ClassTag[T]): Map[Query, Iterable[T]] = {
    session.events.flatMap {
      case e if tag.runtimeClass == e.getClass => Some(e.asInstanceOf[T])
      case _ => None
    }
      .flatMap { event =>
        getQuery(event) match {
          case Some(query) => Some((event, query))
          case None => None
        }
      }
      .groupBy {
        case (_, query) => query
      }
      .mapValues(events => events.map {
        case (event, _) => event
      })
  }
}
