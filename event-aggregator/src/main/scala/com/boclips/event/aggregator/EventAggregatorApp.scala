package com.boclips.event.aggregator

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.config._
import com.boclips.event.aggregator.domain.model.ContractLegalRestriction
import com.boclips.event.aggregator.domain.model.collections.Collection
import com.boclips.event.aggregator.domain.model.contentpackages.{ContentPackage, ContentPackageId}
import com.boclips.event.aggregator.domain.model.contentpartners.{Channel, Contract}
import com.boclips.event.aggregator.domain.model.events.{CollectionInteractedWithEvent, Event, PageRenderedEvent, PlatformInteractedWithEvent}
import com.boclips.event.aggregator.domain.model.okrs.{Monthly, Weekly}
import com.boclips.event.aggregator.domain.model.orders.Order
import com.boclips.event.aggregator.domain.model.playbacks.Playback
import com.boclips.event.aggregator.domain.model.search.{Search, VideoSearchResultImpression}
import com.boclips.event.aggregator.domain.model.sessions.Session
import com.boclips.event.aggregator.domain.model.users.User
import com.boclips.event.aggregator.domain.model.videos._
import com.boclips.event.aggregator.domain.service.Data
import com.boclips.event.aggregator.domain.service.collection.{CollectionInteractionEventAssembler, CollectionSearchResultImpressionAssembler}
import com.boclips.event.aggregator.domain.service.navigation.{PagesRenderedAssembler, PlatformInteractedWithEventAssembler}
import com.boclips.event.aggregator.domain.service.okr.OkrService
import com.boclips.event.aggregator.domain.service.playback.PlaybackAssembler
import com.boclips.event.aggregator.domain.service.search.{QueryScorer, SearchAssembler}
import com.boclips.event.aggregator.domain.service.session.SessionAssembler
import com.boclips.event.aggregator.domain.service.storage.StorageChargesAssembler
import com.boclips.event.aggregator.domain.service.user.UserAssembler
import com.boclips.event.aggregator.domain.service.video.{VideoInteractionAssembler, VideoSearchResultImpressionAssembler}
import com.boclips.event.aggregator.infrastructure.bigquery.BigQueryTableWriter
import com.boclips.event.aggregator.infrastructure.mongo.{MongoChannelLoader, MongoCollectionLoader, MongoContentPackageLoader, MongoContractLegalRestrictionLoader, MongoContractLoader, MongoEventLoader, MongoOrderLoader, MongoUserLoader, MongoVideoLoader, SparkMongoClient}
import com.boclips.event.aggregator.infrastructure.videoservice.ContentPackageMetricsClient
import com.boclips.event.aggregator.infrastructure.youtube.YouTubeService
import com.boclips.event.aggregator.presentation.TableNames.VIDEOS
import com.boclips.event.aggregator.presentation.assemblers.{CollectionTableRowAssembler, ContractTableRowAssembler, UserTableRowAssembler, VideoTableRowAssembler}
import com.boclips.event.aggregator.presentation.formatters.{ChannelFormatter, CollectionFormatter, ContractFormatter, DataVersionFormatter, VideoFormatter}
import com.boclips.event.aggregator.presentation.model.VideoTableRow
import com.boclips.event.aggregator.presentation.{RowFormatter, TableFormatter, TableWriter}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.neo4j.spark.dataframe.Neo4jDataFrame
import org.slf4j.{Logger, LoggerFactory}

import scala.reflect.runtime.universe.TypeTag

object EventAggregatorApp {
  def main(args: Array[String]): Unit = {
    val neo4jConfig = Neo4jConfig.fromEnv
    val sparkConfig: SparkConfig = SparkConfig(neo4jConfig)
    implicit val session: SparkSession = sparkConfig.session
    val writer = new BigQueryTableWriter(BigQueryConfig())
    val mongoSparkProvider = new SparkMongoClient(MongoConfig())
    new EventAggregatorApp(
      writer,
      mongoSparkProvider,
      EventAggregatorConfig(
        neo4j = neo4jConfig,
        youTube = Some(YouTubeConfig.fromEnv),
        contentPackageMetrics = Some(ContentPackageMetricsConfig.fromEnv)
      )
    ).run()
  }

  def getVideoIdsForContentPackages(
                                     session: SparkSession,
                                     contentPackages: RDD[ContentPackage],
                                     config: ContentPackageMetricsConfig
                                   ): RDD[(ContentPackageId, VideoId)] = {
    def printContentPackagePage(name: String, page: Int, videoCount: Int): Unit = {
      println(s"Content package ${name}, page #${page}: Got ${videoCount} videos")
    }

    contentPackages
      .mapPartitions { chunk =>
        val client = ContentPackageMetricsClient(config)
        chunk.flatMap { contentPackage =>
          var result = client.getVideoIdsForContentPackage(contentPackage)
          printContentPackagePage(contentPackage.name, 0, result.videoIds.length)
          val requestCursor = result.cursor
          var videoIds = result.videoIds
          var i = 1
          while (result.videoIds.nonEmpty) {
            result = client.getVideoIdsForContentPackage(contentPackage, requestCursor)
            printContentPackagePage(contentPackage.name, i, result.videoIds.length)
            videoIds = videoIds ++ result.videoIds
            i += 1
          }
          videoIds.map(videoId => (contentPackage.id, videoId))
        }
      }
      .persist(StorageLevel.MEMORY_AND_DISK)
      .setName("Content Packages")
  }
}

class EventAggregatorApp(
                          val writer: TableWriter,
                          val mongoClient: SparkMongoClient,
                          val configuration: EventAggregatorConfig = EventAggregatorConfig()
                        )(implicit val session: SparkSession) {
  val log: Logger = LoggerFactory.getLogger(classOf[EventAggregatorApp])

  val userLoader = new MongoUserLoader(mongoClient)
  val events: RDD[Event] = new MongoEventLoader(mongoClient, userLoader.loadAllUsers, userLoader.loadBoclipsEmployees).load
  val users: RDD[User] = UserAssembler(userLoader.loadAllUsers, events)
  val videos: RDD[Video] = new MongoVideoLoader(mongoClient).load()
  val collections: RDD[Collection] = new MongoCollectionLoader(mongoClient).load()
  val channels: RDD[Channel] = new MongoChannelLoader(mongoClient).load()
  val contracts: RDD[Contract] = new MongoContractLoader(mongoClient).load()
  val orders: RDD[Order] = new MongoOrderLoader(mongoClient).load()
  val contentPackages: RDD[ContentPackage] = new MongoContentPackageLoader(mongoClient).load()
  val contractLegalRestrictions: RDD[ContractLegalRestriction] = new MongoContractLegalRestrictionLoader(mongoClient).load()

  val sessions: RDD[Session] = new SessionAssembler(events, "all data").assembleSessions()
  val playbacks: RDD[Playback] = new PlaybackAssembler(sessions, videos).assemblePlaybacks()
  val searches: RDD[Search] = new SearchAssembler(sessions).assembleSearches()
  val storageCharges: RDD[VideoStorageCharge] = new StorageChargesAssembler(videos).assembleStorageCharges

  def run(): Unit = {
    logProcessingStart(s"Getting YouTube statistics")
    val youtubeStatsByVideoPlaybackId: RDD[YouTubeVideoStats] = getYoutubeVideoStats
    logProcessingStart(s"Assembling video search results")
    val impressions: RDD[VideoSearchResultImpression] =
      VideoSearchResultImpressionAssembler(searches)
    logProcessingStart(s"Assembling video interactions results")
    val videoInteractions = VideoInteractionAssembler(events)
    logProcessingStart(s"Assembling contracts")
    val contractsWithRelatedData = ContractTableRowAssembler.assembleContractsWithRelatedData(contracts, contractLegalRestrictions)
    logProcessingStart(s"Getting content packages")
    val videoIdsForContentPackages: RDD[(ContentPackageId, VideoId)] = session.sparkContext.emptyRDD
    //      configuration.contentPackageMetrics.map(config =>
    //      getVideoIdsForContentPackages(
    //        session,
    //        contentPackages,
    //        config
    //      )
    //    )
    //      .getOrElse(session.sparkContext.emptyRDD)
    println(s"Completed content packages preparation. Got ${videoIdsForContentPackages.count()} content package ID pairs")
    logProcessingStart(s"Assembling videos")
    val videosWithRelatedData = VideoTableRowAssembler.assembleVideosWithRelatedData(
      videos,
      playbacks,
      users,
      orders,
      channels,
      contractsWithRelatedData,
      collections,
      impressions,
      videoInteractions,
      youtubeStatsByVideoPlaybackId,
      contentPackages,
      videoIdsForContentPackages
    )

    writeTable(videosWithRelatedData, VIDEOS)(VideoFormatter, implicitly)

    logProcessingStart(s"Updating contracts")
    writeTable(contractsWithRelatedData, "contracts")(ContractFormatter, implicitly)

    logProcessingStart(s"Updating channels")
    writeTable(channels, "channels")(ChannelFormatter, implicitly)

    logProcessingStart(s"Updating collections")
    val collectionImpressions = CollectionSearchResultImpressionAssembler(searches)
    val collectionInteractions: RDD[CollectionInteractedWithEvent] = CollectionInteractionEventAssembler(events)
    val collectionsWithRelatedData = CollectionTableRowAssembler.assembleCollectionsWithRelatedData(collections, collectionImpressions, collectionInteractions)
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
    val usersWithStatus = UserTableRowAssembler(users, playbacks, searches, sessions)
    writeTable(usersWithStatus, "users")

    logProcessingStart(s"Updating user navigation")
    val pagesRendered: RDD[PageRenderedEvent] = new PagesRenderedAssembler(events).assemblePagesRendered()
    writeTable(pagesRendered, "user_navigation")

    logProcessingStart(s"Updating Platform Generic Interactions")
    val platformInteractedWithEvents: RDD[PlatformInteractedWithEvent] =
      new PlatformInteractedWithEventAssembler(events).assemblePlatformInteractedWithEvents()
    writeTable(platformInteractedWithEvents, "platform_interacted_with_events")

    logProcessingStart(s"Updating collection interaction")
    val collectionInteractionEvents: RDD[CollectionInteractedWithEvent] = CollectionInteractionEventAssembler(events)
    writeTable(collectionInteractionEvents, "collection_interactions")

    logProcessingStart(s"Updating data version")
    val timestampInRdd = session.sparkContext.parallelize(List(ZonedDateTime.now(ZoneOffset.UTC)))
    writeTable(timestampInRdd, "data_version")(DataVersionFormatter, implicitly)
  }

  private def writeTable[T](
                             data: RDD[T],
                             tableName: String
                           )(implicit formatter: RowFormatter[T], typeTag: TypeTag[T]): Unit = {
    val tableFormatter = new TableFormatter[T](formatter)
    val jsonData = tableFormatter.formatRowsAsJson(data)
    val schema = tableFormatter.schema()
    logProcessingStart(s"Writing table ${tableName}")
    writer.writeTable(jsonData, schema, tableName)
  }

  private def getYoutubeVideoStats: RDD[YouTubeVideoStats] =
    configuration.youTube.map { youTubeConfig =>
      val youtubeVideoIdsByPlaybackId: RDD[(String, VideoId)] =
        videos
          .filter(_.playbackProvider == "YOUTUBE")
          .map(v => (v.playbackId, v.id))
      youtubeVideoIdsByPlaybackId
        .mapPartitions(idPairs => {
          YouTubeService(
            youTubeConfig
          ).getVideoStats(
            idPairs.toMap
          ).iterator
        })
    }.getOrElse(session.sparkContext.parallelize(List()))

  private def writeToGraph(rows: RDD[VideoTableRow]): Unit = {
    val videoRows: RDD[Row] = rows.map(row =>
      Row(
        row.video.id.value,
        row.video.title,
        row.channel.map(_.id.value).orNull,
        row.channel.map(_.name).orNull
      )
    )
    val structType = StructType(List(
      StructField("videoId", StringType, nullable = false),
      StructField("videoTitle", StringType, nullable = false),
      StructField("channelId", StringType),
      StructField("channelName", StringType),
    ))
    val videoDataFrame = session.createDataFrame(videoRows, structType)
    Neo4jDataFrame.mergeEdgeList(
      session.sparkContext,
      videoDataFrame.where("channelId IS NOT NULL"),
      source = ("Video", Seq("videoId", "videoTitle")),
      relationship = ("BELONGS_TO_CHANNEL", Nil),
      target = ("Channel", Seq("channelId", "channelName")),
      renamedColumns = Map(
        ("videoId", "uuid"),
        ("videoTitle", "title"),
        ("channelId", "uuid"),
        ("channelName", "name"),
      )
    )

    val topicRows = rows.flatMap(row =>
      row.video.topics.map(topic => {
        val parent = topic.parent
        val grandparent = parent.flatMap(_.parent)
        Row(
          row.video.id.value,
          topic.name,
          parent.map(_.name).orNull,
          grandparent.map(_.name).orNull,
        )
      })
    )
    val topicStructType = StructType(List(
      StructField("videoId", StringType, nullable = false),
      StructField("topic", StringType),
      StructField("parent", StringType),
      StructField("grandparent", StringType)
    ))
    val topicDataFrame = session.createDataFrame(topicRows, topicStructType)
    Neo4jDataFrame.mergeEdgeList(
      session.sparkContext,
      topicDataFrame.where("topic IS NOT NULL"),
      source = ("Video", Seq("videoId")),
      relationship = ("HAS_TOPIC", Nil),
      target = ("Topic", Seq("topic")),
      renamedColumns = Map(
        ("videoId", "uuid"),
        ("topic", "name")
      )
    )
    Neo4jDataFrame.mergeEdgeList(
      session.sparkContext,
      topicDataFrame.where(
        "topic IS NOT NULL AND parent IS NOT NULL"
      ),
      source = ("Topic", Seq("topic")),
      relationship = ("HAS_PARENT_TOPIC", Nil),
      target = ("Topic", Seq("parent")),
      renamedColumns = Map(
        ("topic", "name"),
        ("parent", "name")
      )
    )
    Neo4jDataFrame.mergeEdgeList(
      session.sparkContext,
      topicDataFrame.where(
        "parent IS NOT NULL AND grandparent IS NOT NULL"
      ),
      source = ("Topic", Seq("parent")),
      relationship = ("HAS_PARENT_TOPIC", Nil),
      target = ("Topic", Seq("grandparent")),
      renamedColumns = Map(
        ("parent", "name"),
        ("grandparent", "name")
      )
    )
  }

  private def logProcessingStart(name: String): Unit = {
    log.info(name)
    session.sparkContext.setJobGroup(name, name)
  }
}
