package com.boclips.event.aggregator.infrastructure.mongo

import java.util

import com.boclips.event.aggregator.config.{MongoConfig, MongoCredentials}
import com.mongodb.client.MongoCollection
import com.mongodb.spark.DefaultHelper.DefaultsTo

import scala.collection.JavaConverters._
import com.mongodb.{MongoClient, MongoClientOptions, MongoCredential, ServerAddress}
import com.mongodb.spark.{MongoClientFactory, MongoConnector, MongoSpark}
import com.mongodb.spark.config.ReadConfig
import com.mongodb.spark.rdd.MongoRDD
import org.apache.spark.sql.SparkSession
import org.bson.Document
import org.bson.codecs.configuration.{CodecRegistries, CodecRegistry}
import org.bson.codecs.pojo.PojoCodecProvider

import scala.reflect.ClassTag

class SparkMongoClient(config: MongoConfig) {

  def collectionRDD[TDocument: ClassTag](collectionName: String)(implicit session: SparkSession, e: TDocument DefaultsTo Document): MongoRDD[TDocument] = {
    val opts = new util.HashMap[String, String]()
    opts.put("spark.mongodb.input.database", config.database)
    opts.put("spark.mongodb.input.collection", collectionName)

    MongoSpark
      .builder()
      .sparkContext(session.sparkContext)
      .connector(new MongoConnector(clientFactory()))
      .readConfig(ReadConfig.create(opts))
      .build()
      .toRDD()
  }

  def collection[TDocument : ClassTag](collectionName: String)(implicit e: TDocument DefaultsTo Document): MongoCollection[TDocument] = {
    val classTag = implicitly[reflect.ClassTag[TDocument]]
    val documentClass = classTag.runtimeClass.asInstanceOf[Class[TDocument]]
    clientFactory().create().getDatabase(config.database).getCollection(collectionName, documentClass)
  }

  private def clientFactory(): MongoClientFactory = CustomCodecMongoClientFactory(config)
}

private [mongo] case class CustomCodecMongoClientFactory(config: MongoConfig) extends MongoClientFactory {
  override def create(): MongoClient = {
    val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
      MongoClient.getDefaultCodecRegistry,
      CodecRegistries.fromProviders(PojoCodecProvider.builder.automatic(true).build()),
    )
    val optionalCredential = config.credentials.map {
      case MongoCredentials(authenticationDatabase, username, password) =>
        MongoCredential.createCredential(username, authenticationDatabase, password.toCharArray)
    }
    val options = new MongoClientOptions.Builder()
      .codecRegistry(codecRegistry)
      .retryWrites(true)
      .sslEnabled(config.ssl)
      .requiredReplicaSetName(config.replicaSetName.orNull)
      .build()

    val serverAddresses = config.serverAddresses.asJava

    optionalCredential match {
      case Some(credential) => new MongoClient(serverAddresses, credential, options)
      case None => new MongoClient(serverAddresses, options)
    }


  }
}
