package com.boclips.event.aggregator

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.config.{MongoConfig, SparkConfig}
import com.boclips.event.aggregator.domain.model._
import com.boclips.event.aggregator.domain.model.events.{CollectionInteractedWithEvent, Event, PageRenderedEvent}
import com.boclips.event.aggregator.domain.service.Data
import com.boclips.event.aggregator.domain.service.collection.{CollectionAssembler, CollectionInteractionEventAssembler, CollectionSearchResultImpressionAssembler}
import com.boclips.event.aggregator.domain.service.contentpartners.ContentPartnerAssembler
import com.boclips.event.aggregator.domain.service.navigation.PagesRenderedAssembler
import com.boclips.event.aggregator.domain.service.okr.OkrService
import com.boclips.event.aggregator.domain.service.playback.PlaybackAssembler
import com.boclips.event.aggregator.domain.service.search.{QueryScorer, SearchAssembler}
import com.boclips.event.aggregator.domain.service.session.SessionAssembler
import com.boclips.event.aggregator.domain.service.storage.StorageChargesAssembler
import com.boclips.event.aggregator.domain.service.user.UserWithRelatedDataAssembler
import com.boclips.event.aggregator.domain.service.video.{VideoAssembler, VideoInteractionAssembler, VideoSearchResultImpressionAssembler}
import com.boclips.event.aggregator.infrastructure.bigquery.BigQueryTableWriter
import com.boclips.event.aggregator.infrastructure.mongo.{MongoChannelLoader, MongoCollectionLoader, MongoEventLoader, MongoOrderLoader, SparkMongoClient, MongoUserLoader, MongoVideoLoader}
import com.boclips.event.aggregator.presentation.formatters.{CollectionFormatter, DataVersionFormatter, VideoFormatter}
import com.boclips.event.aggregator.presentation.{RowFormatter, TableFormatter, TableWriter}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

import scala.reflect.runtime.universe.TypeTag

object EventAggregatorApp {

  def main(args: Array[String]): Unit = {
    val sparkConfig: SparkConfig = SparkConfig()
    implicit val session: SparkSession = sparkConfig.session
    val writer = new BigQueryTableWriter(session)
    val mongoSparkProvider = new SparkMongoClient(MongoConfig())
    new EventAggregatorApp(writer, mongoSparkProvider).run()
  }
}

class EventAggregatorApp(val writer: TableWriter, val mongoClient: SparkMongoClient)(implicit val session: SparkSession) {

  val log: Logger = LoggerFactory.getLogger(classOf[EventAggregatorApp])

  val userLoader = new MongoUserLoader(mongoClient)
  val events: RDD[Event] = new MongoEventLoader(mongoClient, userLoader.loadBoclipsEmployees).load
  val users: RDD[User] = userLoader.loadAllUsers
  val videos: RDD[Video] = new MongoVideoLoader(mongoClient).load()
  val collections: RDD[Collection] = new MongoCollectionLoader(mongoClient).load()
  val channels: RDD[Channel] = new MongoChannelLoader(mongoClient).load()
  val orders: RDD[Order] = new MongoOrderLoader(mongoClient).load()

  val sessions: RDD[Session] = new SessionAssembler(events, users, "all data").assembleSessions()
  val playbacks: RDD[Playback] = new PlaybackAssembler(sessions, videos).assemblePlaybacks()
  val searches: RDD[Search] = new SearchAssembler(sessions).assembleSearches()
  val storageCharges: RDD[VideoStorageCharge] = new StorageChargesAssembler(videos).assembleStorageCharges

  private def writeTable[T](data: RDD[T], tableName: String)(implicit formatter: RowFormatter[T], typeTag: TypeTag[T]): Unit = {
    val tableFormatter = new TableFormatter[T](formatter)
    val jsonData = tableFormatter.formatRowsAsJson(data)
    val schema = tableFormatter.schema()
    writer.writeTable(jsonData, schema, tableName)
  }

  def run(): Unit = {
    logProcessingStart(s"Updating videos")
    val impressions = VideoSearchResultImpressionAssembler(searches)
    val videoInteractions = VideoInteractionAssembler(events)
    val videosWithRelatedData = VideoAssembler.assembleVideosWithRelatedData(videos, playbacks, orders, channels, impressions, videoInteractions)
    writeTable(videosWithRelatedData, "videos")(VideoFormatter, implicitly)

    logProcessingStart(s"Updating content partners")
    val contentPartners = ContentPartnerAssembler(videos)
    writeTable(contentPartners, "content_partners")

    logProcessingStart(s"Updating collections")
    val collectionImpressions = CollectionSearchResultImpressionAssembler(searches)
    val collectionInteractions: RDD[CollectionInteractedWithEvent] = CollectionInteractionEventAssembler(events)
    val collectionsWithRelatedData = CollectionAssembler.assembleCollectionsWithRelatedData(collections, collectionImpressions, collectionInteractions)
    writeTable(collectionsWithRelatedData, "collections")(CollectionFormatter, implicitly)

    logProcessingStart(s"Updating video impressions")
    writeTable(impressions, "video_search_result_impressions")

    logProcessingStart(s"Updating key results")
    val schoolData = Data(events, users, videos).schoolOnly()
    val keyResults = session.sparkContext.parallelize(OkrService.computeKeyResults(Monthly())(schoolData))
    writeTable(keyResults, "key_results_monthly")

    logProcessingStart(s"Updating query scores")
    val queryScorer = new QueryScorer(priorHits = 3, priorMisses = 7)
    writeTable(queryScorer.scoreQueries(searches, Monthly()), "query_scores_monthly")
    writeTable(queryScorer.scoreQueries(searches, Weekly()), "query_scores_weekly")

    logProcessingStart(s"Updating users")
    val usersWithStatus = UserWithRelatedDataAssembler(users, playbacks, searches, sessions)
    writeTable(usersWithStatus, "users")

    logProcessingStart(s"Updating user navigation")
    val pagesRendered: RDD[PageRenderedEvent] = new PagesRenderedAssembler(events).assemblePagesRendered()
    writeTable(pagesRendered, "user_navigation")

    logProcessingStart(s"Updating collection interaction")
    val collectionInteractionEvents: RDD[CollectionInteractedWithEvent] = CollectionInteractionEventAssembler(events)
    writeTable(collectionInteractionEvents, "collection_interactions")

    logProcessingStart(s"Updating data version")
    val timestampInRdd = session.sparkContext.parallelize(List(ZonedDateTime.now(ZoneOffset.UTC)))
    writeTable(timestampInRdd, "data_version")(DataVersionFormatter, implicitly)
  }

  private def logProcessingStart(name: String): Unit = {
    log.info(name)
    session.sparkContext.setJobGroup(name, name)
  }
}



