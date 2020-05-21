package com.boclips.event.aggregator.infrastructure

import java.time.{ZoneOffset, ZonedDateTime}

import org.bson.Document

import scala.collection.JavaConverters._

package object mongo {

  implicit class ExtendedBsonDocument(document: Document) {

    def getObject(property: String): Document = {
      new Document(document.get(property).asInstanceOf[java.util.Map[String, Object]])
    }

    def getDateTime(property: String): ZonedDateTime = {
      ZonedDateTime.ofInstant(document.getDate(property).toInstant, ZoneOffset.UTC)
    }

    def getList[T](property: String): List[T] = {
      getListOption[T](property)
        .getOrElse(List())
    }

    def getListOption[T](property: String): Option[List[T]] = {
      Option(document.get(property).asInstanceOf[java.util.List[T]])
        .map(_.asScala.toList)
    }

    def getStringOption(property: String): Option[String] = {
      Option(document.getString(property))
    }

    def getIntOption(property: String): Option[Int] = {
      Option(document.getInteger(property)).map(_.asInstanceOf[Int])
    }

    def getBooleanOption(property: String): Option[Boolean] = {
      Option(document.getBoolean(property)).map(_.asInstanceOf[Boolean])
    }
  }

}
