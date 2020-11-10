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

    def getScalaList[T](property: String): List[T] = {
      getListOption[T](property)
        .getOrElse(List())
    }

    def getObjectOption(property: String): Option[Document] = {
      Option(document.get(property)) match {
        case Some(documentObj) => Option(new Document(documentObj.asInstanceOf[java.util.Map[String, Object]]))
        case None => None
      }
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

  def integerOption(x: java.lang.Integer): Option[Int] = {
    x match {
      case null => None
      case _ => Some(x)
    }
  }

  def booleanOption(x: java.lang.Boolean): Option[Boolean] = {
    x match {
      case null => None
      case _ => Some(x)
    }
  }
}
