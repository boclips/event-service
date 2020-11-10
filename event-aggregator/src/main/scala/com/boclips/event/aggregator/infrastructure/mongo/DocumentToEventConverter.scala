package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.collections.CollectionId
import com.boclips.event.aggregator.domain.model.events._
import com.boclips.event.aggregator.domain.model.search.Query
import com.boclips.event.aggregator.domain.model.users._
import com.boclips.event.aggregator.domain.model.videos.VideoId
import com.boclips.event.aggregator.infrastructure.model.EventDocumentWithIdentity
import com.boclips.event.infrastructure.EventFields
import com.boclips.event.infrastructure.EventFields._
import org.bson.Document

import scala.collection.JavaConverters.{iterableAsScalaIterableConverter, mapAsScalaMapConverter}

object DocumentToEventConverter {

  implicit class DocumentExtensions(document: Document) {
    def url: Option[Url] = Option(document.getString(URL)).map(Url.parse)

    def queryFromUrl: Option[Query] = url.flatMap(_.param("q")).map(Query)
  }

  def convert(eventWrapper: EventDocumentWithIdentity): Event = {
    val eventType = eventWrapper.eventDocument.getString(TYPE)
    eventType match {
      case Type.VIDEOS_SEARCHED => convertSearchEvent(eventWrapper)
      case Type.RESOURCES_SEARCHED => convertCollectionsSearched(eventWrapper)
      case Type.VIDEO_SEGMENT_PLAYED => convertVideoSegmentPlayedEvent(eventWrapper)
      case Type.VIDEO_INTERACTED_WITH => convertVideoInteractedWithEvent(eventWrapper)
      case Type.VIDEO_ADDED_TO_COLLECTION => convertVideoAddedToCollectionEvent(eventWrapper)
      case Type.PAGE_RENDERED => convertPageRenderedEvent(eventWrapper)
      case Type.COLLECTION_INTERACTED_WITH => convertCollectionInteractedWithEvent(eventWrapper)
      case Type.PLATFORM_INTERACTED_WITH => convertPlatformInteractedWithEvent(eventWrapper)
      case _ => convertOtherEvent(eventWrapper, eventType)
    }
  }

  def convertVideoSegmentPlayedEvent(eventDocumentWithIdentity: EventDocumentWithIdentity): Event = {
    val document = eventDocumentWithIdentity.eventDocument
    val url = document.url
    val userIdentity = eventDocumentWithIdentity.userIdentity
    val videoIndex = document.getInteger(PLAYBACK_VIDEO_INDEX) match {
      case null => None
      case index => Some(index.toInt)
    }

    val videoId = VideoId(document.getString(VIDEO_ID))

    if (videoId.value == null) {
      convertOtherEvent(eventDocumentWithIdentity, "OTHER_PLAYBACK")
    } else {
      val query = document.getStringOption(EventFields.SEARCH_QUERY) match {
        case None => None
        case Some(queryString) => Some(Query(queryString))
      }

      VideoSegmentPlayedEvent(
        id = document.getObjectId("_id").toHexString,
        timestamp = ZonedDateTime.ofInstant(document.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC),
        userIdentity = userIdentity,
        url = url,
        query = if (query.isDefined) query else url.flatMap(_.param("q")).map(Query),
        refererId = url.flatMap(_.param("referer")).map(UserId),
        videoId = videoId,
        videoIndex = videoIndex,
        secondsWatched = Math.max(0, document.getLong(PLAYBACK_SEGMENT_END_SECONDS).toInt - document.getLong(PLAYBACK_SEGMENT_START_SECONDS).toInt)
      )
    }
  }

  def convertSearchEvent(eventDocumentWithIdentity: EventDocumentWithIdentity): Event = {
    val document = eventDocumentWithIdentity.eventDocument
    val query = document.getString(SEARCH_QUERY)
    if (query == null) {
      convertOtherEvent(eventDocumentWithIdentity, "OTHER_SEARCH")
    } else {
      VideosSearchedEvent(
        timestamp = ZonedDateTime.ofInstant(document.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC),
        userIdentity = eventDocumentWithIdentity.userIdentity,
        query = Query(query),
        url = document.url,
        videoResults = document.getListOption[String](SEARCH_RESULTS_PAGE_VIDEO_IDS).map(_.map(VideoId)),
        pageIndex = document.getInteger(SEARCH_RESULTS_PAGE_INDEX, 0),
        totalResults = document.getLong(SEARCH_RESULTS_TOTAL).toInt,
        queryParams = convertQueryParams(document)
      )
    }
  }

  def convertQueryParams(document: Document): Option[collection.mutable.Map[String, Iterable[String]]] = {
    document.getObjectOption(SEARCH_QUERY_PARAMS) match {
      case None => None
      case Some(queryParams) =>
        val valuesAsAnyMap: Map[String, AnyRef] = queryParams.asScala.toMap

        val valuesAsListMap: Map[String, Iterable[String]] = valuesAsAnyMap.mapValues(
          value => value.asInstanceOf[java.util.List[String]].asScala.toList
        )
        Some(collection.mutable.Map(valuesAsListMap.toSeq: _*))
    }
  }

  def convertCollectionsSearched(eventDocumentWithIdentity: EventDocumentWithIdentity): Event = {
    val document = eventDocumentWithIdentity.eventDocument
    CollectionSearchedEvent(
      timestamp = ZonedDateTime.ofInstant(document.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC),
      query = Query(document.getString(SEARCH_QUERY)),
      userIdentity = eventDocumentWithIdentity.userIdentity,
      url = document.url,
      collectionResults = document.getScalaList[String](SEARCH_RESULTS_PAGE_RESOURCE_IDS).map(CollectionId),
      pageIndex = document.getInteger(SEARCH_RESULTS_PAGE_INDEX),
      pageSize = document.getInteger(SEARCH_RESULTS_PAGE_SIZE),
      totalResults = document.getLong(SEARCH_RESULTS_TOTAL).toInt
    )
  }

  def convertOtherEvent(eventDocumentWithIdentity: EventDocumentWithIdentity, eventType: String) = OtherEvent(
    timestamp = ZonedDateTime.ofInstant(eventDocumentWithIdentity.eventDocument.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC),
    userIdentity = eventDocumentWithIdentity.userIdentity,
    typeName = eventType
  )

  def convertVideoInteractedWithEvent(eventDocumentWithIdentity: EventDocumentWithIdentity): VideoInteractedWithEvent = {
    val document = eventDocumentWithIdentity.eventDocument
    val timestamp = ZonedDateTime.ofInstant(document.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC)
    val url = document.url
    VideoInteractedWithEvent(
      url = url,
      timestamp = timestamp,
      userIdentity = eventDocumentWithIdentity.userIdentity,
      videoId = VideoId(document.getString(VIDEO_ID)),
      query = document.queryFromUrl,
      subtype = document.getStringOption(SUBTYPE)
    )
  }

  def convertCollectionInteractedWithEvent(eventDocumentWithIdentity: EventDocumentWithIdentity): CollectionInteractedWithEvent = {
    val document = eventDocumentWithIdentity.eventDocument
    val timestamp = ZonedDateTime.ofInstant(document.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC)
    val url = document.url

    CollectionInteractedWithEvent(
      url = url,
      timestamp = timestamp,
      userIdentity = eventDocumentWithIdentity.userIdentity,
      query = document.queryFromUrl,
      subtype = document.getStringOption(SUBTYPE),
      collectionId = CollectionId(document.getString(COLLECTION_ID))
    )
  }


  def convertVideoAddedToCollectionEvent(eventDocumentWithIdentity: EventDocumentWithIdentity): VideoAddedToCollectionEvent = {
    val document = eventDocumentWithIdentity.eventDocument
    val timestamp = ZonedDateTime.ofInstant(document.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC)
    VideoAddedToCollectionEvent(
      timestamp = timestamp,
      userIdentity = eventDocumentWithIdentity.userIdentity.asInstanceOf[BoclipsUserIdentity],
      videoId = VideoId(document.getString(VIDEO_ID)),
      url = document.url,
      query = document.queryFromUrl
    )
  }

  def convertPageRenderedEvent(eventDocumentWithIdentity: EventDocumentWithIdentity): PageRenderedEvent = {
    val document = eventDocumentWithIdentity.eventDocument
    val timestamp = ZonedDateTime.ofInstant(document.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC)
    PageRenderedEvent(
      timestamp = timestamp,
      userIdentity = eventDocumentWithIdentity.userIdentity,
      url = document.url
    )
  }

  def convertPlatformInteractedWithEvent(eventDocumentWithIdentity: EventDocumentWithIdentity): PlatformInteractedWithEvent = {
    val document = eventDocumentWithIdentity.eventDocument
    val timestamp = ZonedDateTime.ofInstant(document.getDate(TIMESTAMP).toInstant, ZoneOffset.UTC)
    PlatformInteractedWithEvent(
      userIdentity = eventDocumentWithIdentity.userIdentity,
      timestamp = timestamp,
      url = document.url,
      subtype = document.getStringOption(SUBTYPE),
    )
  }
}
