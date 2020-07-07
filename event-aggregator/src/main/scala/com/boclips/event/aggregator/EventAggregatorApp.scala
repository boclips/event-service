package com.boclips.event.aggregator

//import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.config.{BigQueryConfig, MongoConfig, SparkConfig}
import com.boclips.event.aggregator.domain.model._
//import com.boclips.event.aggregator.domain.model.events.{CollectionInteractedWithEvent, Event, PageRenderedEvent, PlatformInteractedWithEvent}
//import com.boclips.event.aggregator.domain.service.Data
//import com.boclips.event.aggregator.domain.service.collection.{CollectionAssembler, CollectionInteractionEventAssembler, CollectionSearchResultImpressionAssembler}
//import com.boclips.event.aggregator.domain.service.navigation.{PagesRenderedAssembler, PlatformInteractedWithEventAssembler}
//import com.boclips.event.aggregator.domain.service.okr.OkrService
//import com.boclips.event.aggregator.domain.service.playback.{PlaybackAssembler, PlaybackWithRelatedDataAssembler}
//import com.boclips.event.aggregator.domain.service.search.{QueryScorer, SearchAssembler}
//import com.boclips.event.aggregator.domain.service.session.SessionAssembler
//import com.boclips.event.aggregator.domain.service.storage.StorageChargesAssembler
//import com.boclips.event.aggregator.domain.service.user.UserWithRelatedDataAssembler
//import com.boclips.event.aggregator.domain.service.video.{VideoAssembler, VideoInteractionAssembler, VideoSearchResultImpressionAssembler}
import com.boclips.event.aggregator.infrastructure.bigquery.BigQueryTableWriter
import com.boclips.event.aggregator.infrastructure.mongo.{MongoChannelLoader, MongoVideoLoader, SparkMongoClient}
//import com.boclips.event.aggregator.presentation.formatters.{ChannelFormatter, CollectionFormatter, ContractFormatter, DataVersionFormatter, VideoFormatter}
import com.boclips.event.aggregator.presentation.{RowFormatter, TableWriter}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.neo4j.spark.Neo4j
import org.slf4j.{Logger, LoggerFactory}

import scala.reflect.runtime.universe.TypeTag

sealed trait GraphNode

case class VideoNode(video: Video) extends GraphNode

case class ChannelNode(channel: Channel) extends GraphNode

object EventAggregatorApp {

  def main(args: Array[String]): Unit = {
    val sparkConfig: SparkConfig = SparkConfig()
    implicit val session: SparkSession = sparkConfig.session
    val writer = new BigQueryTableWriter(BigQueryConfig())
    val mongoSparkProvider = new SparkMongoClient(MongoConfig())
    val neo4jProvider = Neo4j(session.sparkContext)
    new EventAggregatorApp(writer, neo4jProvider, mongoSparkProvider).run()
  }
}

class EventAggregatorApp(val writer: TableWriter,
                         val neo4jClient: Neo4j,
                         val mongoClient: SparkMongoClient)
                        (implicit val session: SparkSession) {

  val log: Logger = LoggerFactory.getLogger(classOf[EventAggregatorApp])

  // val userLoader = new MongoUserLoader(mongoClient)
  // val events: RDD[Event] = new MongoEventLoader(mongoClient, userLoader.loadBoclipsEmployees).load
  // val users: RDD[User] = userLoader.loadAllUsers
  val videos: RDD[Video] = new MongoVideoLoader(mongoClient).load()
  // val collections: RDD[Collection] = new MongoCollectionLoader(mongoClient).load()
  val channels: RDD[Channel] = new MongoChannelLoader(mongoClient).load()
  // val contracts: RDD[Contract] = new MongoContractLoader(mongoClient).load()
  // val orders: RDD[Order] = new MongoOrderLoader(mongoClient).load()

  // val sessions: RDD[Session] = new SessionAssembler(events, "all data").assembleSessions()
  // val playbacks: RDD[Playback] = new PlaybackAssembler(sessions, videos).assemblePlaybacks()
  // val searches: RDD[Search] = new SearchAssembler(sessions).assembleSearches()
  //val storageCharges: RDD[VideoStorageCharge] = new StorageChargesAssembler(videos).assembleStorageCharges

  private def writeTable[T](data: RDD[T], tableName: String)(implicit formatter: RowFormatter[T], typeTag: TypeTag[T]): Unit = {
    println(s"Not writing table ${tableName}")
    //    val tableFormatter = new TableFormatter[T](formatter)
    //    val jsonData = tableFormatter.formatRowsAsJson(data)
    //    val schema = tableFormatter.schema()
    //    writer.writeTable(jsonData, schema, tableName)
  }

  private def writeVideoGraph(videos: RDD[Video], channels: RDD[Channel]): Unit = {
    val videoNodes: RDD[(Long, VideoNode)] = videos
      .map(VideoNode)
      .zipWithUniqueId()
      .map(_.swap)
    val channelNodes: RDD[(Long, ChannelNode)] = channels
      .map(ChannelNode)
      .zipWithUniqueId()
      .map(_.swap)

    videoNodes.join(channelNodes)

    val videosToChannels: RDD[Edge[Unit]] = videoNodes
      .keyBy(_._2.video.channelId)
      .join(channelNodes.keyBy(_._2.channel.id))
      .values
      .map(it => {
        val videoNodePair: (Long, VideoNode) = it._1
        val channelNodePair: (Long, ChannelNode) = it._2
        Edge(srcId = videoNodePair._1, dstId = channelNodePair._1)
      })

    val graph: Graph[GraphNode, Unit] = Graph(
      vertices =
        videoNodes.asInstanceOf[RDD[(Long, GraphNode)]]
          union channelNodes.asInstanceOf[RDD[(Long, GraphNode)]],
      edges = videosToChannels
    )

    neo4jClient.saveGraph(graph)
  }

  def run(): Unit = {
    //    logProcessingStart(s"Updating videos")
    //    val impressions = VideoSearchResultImpressionAssembler(searches)
    //    val videoInteractions = VideoInteractionAssembler(events)
    //    val playbacksWithRelatedData = new PlaybackWithRelatedDataAssembler(playbacks, users).assemblePlaybacksWithRelatedData()
    //    val videosWithRelatedData = VideoAssembler.assembleVideosWithRelatedData(
    //      videos, playbacksWithRelatedData, orders, channels, contracts, impressions, videoInteractions
    //    )
    //    writeTable(videosWithRelatedData, "videos")(VideoFormatter, implicitly)
    logProcessingStart("Writing video graph")
    writeVideoGraph(videos, channels)

    //    logProcessingStart(s"Updating contracts")
    //    writeTable(contracts, "contracts")(ContractFormatter, implicitly)

    //    logProcessingStart(s"Updating channels")
    //    writeTable(channels, "channels")(ChannelFormatter, implicitly)
    //
    //    logProcessingStart(s"Updating collections")
    //    val collectionImpressions = CollectionSearchResultImpressionAssembler(searches)
    //    val collectionInteractions: RDD[CollectionInteractedWithEvent] = CollectionInteractionEventAssembler(events)
    //    val collectionsWithRelatedData = CollectionAssembler.assembleCollectionsWithRelatedData(collections, collectionImpressions, collectionInteractions)
    //    writeTable(collectionsWithRelatedData, "collections")(CollectionFormatter, implicitly)
    //
    //    logProcessingStart(s"Updating video impressions")
    //    writeTable(impressions, "video_search_result_impressions")
    //
    //    logProcessingStart(s"Updating key results")
    //    val schoolData = Data(events, users, videos).schoolOnly()
    //    val keyResults = session.sparkContext.parallelize(OkrService.computeKeyResults(Monthly())(schoolData))
    //    writeTable(keyResults, "key_results_monthly")
    //
    //    logProcessingStart(s"Updating query scores")
    //    val queryScorer = new QueryScorer(priorHits = 3, priorMisses = 7)
    //    writeTable(queryScorer.scoreQueries(searches, Monthly()), "query_scores_monthly")
    //    writeTable(queryScorer.scoreQueries(searches, Weekly()), "query_scores_weekly")
    //
    //    logProcessingStart(s"Updating users")
    //    val usersWithStatus = UserWithRelatedDataAssembler(users, playbacks, searches, sessions)
    //    writeTable(usersWithStatus, "users")
    //
    //    logProcessingStart(s"Updating user navigation")
    //    val pagesRendered: RDD[PageRenderedEvent] = new PagesRenderedAssembler(events).assemblePagesRendered()
    //    writeTable(pagesRendered, "user_navigation")
    //
    //    logProcessingStart(s"Updating Platform Generic Interactions")
    //    val platformInteractedWithEvents: RDD[PlatformInteractedWithEvent] = new PlatformInteractedWithEventAssembler(events).assemblePlatformInteractedWithEvents()
    //    writeTable(platformInteractedWithEvents, "platform_interacted_with_events")
    //
    //
    //    logProcessingStart(s"Updating collection interaction")
    //    val collectionInteractionEvents: RDD[CollectionInteractedWithEvent] = CollectionInteractionEventAssembler(events)
    //    writeTable(collectionInteractionEvents, "collection_interactions")
    //
    //    logProcessingStart(s"Updating data version")
    //    val timestampInRdd = session.sparkContext.parallelize(List(ZonedDateTime.now(ZoneOffset.UTC)))
    //    writeTable(timestampInRdd, "data_version")(DataVersionFormatter, implicitly)
  }

  private def logProcessingStart(name: String): Unit = {
    log.info(name)
    session.sparkContext.setJobGroup(name, name)
  }
}



