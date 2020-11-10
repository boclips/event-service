package com.boclips.event.aggregator.domain.service.search

import java.util.UUID

import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.events._
import com.boclips.event.aggregator.domain.model.search._
import com.boclips.event.aggregator.domain.model.sessions.Session
import com.boclips.event.aggregator.domain.model.videos.VideoId

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

    val mergedQueryParams = getMergedUrlAndQueryParams(searchEvents)
    val urlParamsKeys = mergedQueryParams.keys.toSet

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
        userIdentity = searchEvent.userIdentity,
        query = searchEvent.query,
        url = searchEvent.url,
        urlParamsKeys = urlParamsKeys,
        queryParams = mergedQueryParams,
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

  def getMergedUrlAndQueryParams(searchEvents: Iterable[VideosSearchedEvent]) = {
    val urlParamsKeys = searchEvents.flatMap(_.url.map(_.params)).flatMap(_.keys).toSet
    val paramsByKey = searchEvents.flatMap(_.queryParams).flatten.groupBy(_._1).mapValues(_.map(_._2).flatten.toSet)
    val mergedQueryParams = collection.mutable.Map(paramsByKey.toSeq: _*)

    urlParamsKeys.diff(mergedQueryParams.keys.toSet).foreach(urlKey => mergedQueryParams.put(urlKey, Set()))
    mergedQueryParams.toMap
  }
}
