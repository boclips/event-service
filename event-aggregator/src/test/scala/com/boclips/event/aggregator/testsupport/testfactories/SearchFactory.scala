package com.boclips.event.aggregator.testsupport.testfactories

import java.time.Month.APRIL
import java.time.ZonedDateTime

import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.okrs.DateRange
import com.boclips.event.aggregator.domain.model.search._
import com.boclips.event.aggregator.domain.model.users.UserIdentity
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.domain.model.{search, _}
import com.boclips.event.aggregator.domain.service.search.SearchResultPlayback
import com.boclips.event.aggregator.testsupport.TestTimestamps.thisYearWhole

object SearchFactory {

  def createSearchRequest(
                           id: String = "id",
                           userIdentity: UserIdentity = UserFactory.createBoclipsUserIdentity(),
                           query: String = "query",
                           timestamp: ZonedDateTime = ZonedDateTime.now(),
                           url: Option[Url] = None,
                           urlParamsKeys: Set[String] = Set(),
                         ): SearchRequest = {
    SearchRequest(
      id = id,
      timestamp = timestamp,
      userIdentity = userIdentity,
      query = Query(query),
      url = url,
      urlParamsKeys = urlParamsKeys,
    )
  }

  def createSearchResponse(
                            videoResults: Set[SearchImpression] = Set(),
                            collectionResults: Set[CollectionImpression] = Set(),
                            totalResults: Int = 777,
                            minResults: Int = 50,
                          ): SearchResponse = {
    SearchResponse(
      videoResults = videoResults,
      collectionResults = collectionResults,
      totalResults = totalResults,
      minResults = minResults,
    )
  }

  def createSearchInteractions(
                                resultPagesSeen: Int = 1,
                                videosPlayed: Iterable[SearchResultPlayback] = List(),
                              ): SearchInteractions = {
    SearchInteractions(
      videosPlayed = videosPlayed,
      resultPagesSeen = resultPagesSeen,
    )
  }

  def createSearch(
                    request: SearchRequest = createSearchRequest(),
                    response: SearchResponse = createSearchResponse(),
                    interactions: SearchInteractions = createSearchInteractions(),
                  ): Search = {
    Search(
      request = request,
      response = response,
      interactions = interactions,
    )
  }

  def createVideoSearchResultImpression(
                                         search: SearchRequest = createSearchRequest(),
                                         videoId: VideoId = VideoId("v1"),
                                         interaction: Boolean = false
                                       ): VideoSearchResultImpression = {
    VideoSearchResultImpression(videoId = videoId, search = search, interaction = interaction)
  }

  def createCollectionSearchResultImpression(
                                              search: SearchRequest = createSearchRequest(),
                                              collectionId: CollectionId = CollectionId("c1"),
                                              interaction: Boolean = false
                                            ): CollectionSearchResultImpression = {
    CollectionSearchResultImpression(collectionId = collectionId, search = search, interaction = interaction)
  }

  def createSearchResultPlayback(videoId: String = "videoId"): SearchResultPlayback = {
    SearchResultPlayback(videoId = VideoId(videoId), videoIndex = None, secondsPlayed = 10)
  }

  def createQueryScore(
                        timePeriod: DateRange = thisYearWhole(APRIL),
                        query: String = "query",
                        score: Double = 0.7,
                        count: Int = 5,
                        hits: Int = 3
                      ): QueryScore = {
    search.QueryScore(
      timePeriod = timePeriod,
      query = query,
      count = count,
      hits = hits,
      score = score
    )
  }
}
