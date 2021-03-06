package com.boclips.event.aggregator.presentation

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.{ISO_DATE_TIME, ISO_LOCAL_DATE}
import java.time.{LocalDate, YearMonth, ZoneOffset, ZonedDateTime}

import com.google.gson.{JsonArray, JsonElement, JsonObject, JsonPrimitive}

import scala.collection.JavaConverters._

package object formatters {

  implicit class ExtendedJsonObject(json: JsonObject) {
    def addDateProperty(property: String, date: LocalDate): Unit = {
      json.addProperty(property, Option(date).map(_.format(ISO_LOCAL_DATE)).orNull)
    }

    def addDateProperty(property: String, date: ZonedDateTime): Unit = {
      json.addProperty(property, Option(date).map(_.format(ISO_LOCAL_DATE)).orNull)
    }

    def addDateTimeProperty(property: String, dateTime: ZonedDateTime): Unit = {
      json.addProperty(property, Option(dateTime).map(_.withZoneSameInstant(ZoneOffset.UTC).withFixedOffsetZone().format(ISO_DATE_TIME)).orNull)
    }

    def addDateTimeProperty(property: String, date: LocalDate): Unit = {
      json.addProperty(property, Option(date).map(_.atStartOfDay(ZoneOffset.UTC).format(ISO_DATE_TIME)).orNull)
    }

    def addMonthProperty(property: String, date: LocalDate): Unit = {
      json.addProperty(property, Option(date).map(_.format(DateTimeFormatter.ofPattern("yyyy-MM"))).orNull)
    }

    def addMonthProperty(property: String, month: YearMonth): Unit = {
      json.addProperty(property, Option(month).map(_.format(DateTimeFormatter.ofPattern("yyyy-MM"))).orNull)
    }

    def addProperty(property: String, value: Option[String]): Unit = {
      json.addProperty(property, value.getOrElse("UNKNOWN"))
    }

    def addStringArrayProperty(property: String, items: List[String]): Unit = {
      addArrayProperty[String](property, items, new JsonPrimitive(_))
    }

    def addStringArrayProperty(property: String, items: Set[String]): Unit = {
      addStringArrayProperty(property, items.toList)
    }

    def addIntArrayProperty(property: String, items: List[Int]): Unit = {
      addArrayProperty[Int](property, items, new JsonPrimitive(_))
    }

    def addBigDecimalArrayProperty(property: String, items: List[BigDecimal]): Unit = {
      addArrayProperty[BigDecimal](property, items, new JsonPrimitive(_))
    }

    def addJsonArrayProperty(property: String, items: List[JsonElement]): Unit = {
      addArrayProperty[JsonElement](property, items, identity)
    }

    private def addArrayProperty[T](property: String, items: List[T], toJson: T => JsonElement): Unit = {
      val jsonArray: JsonArray = items.foldLeft(new JsonArray)((array, element) => {
        array.add(toJson(element))
        array
      })

      json.add(property, jsonArray)
    }

    def getBool(property: String): Boolean = {
      json.get(property).getAsBoolean
    }

    def getString(property: String): String = {
      json.get(property).getAsString
    }

    def getDouble(property: String): Double = {
      json.get(property).getAsDouble
    }

    def getFloat(property: String): Float = {
      json.get(property).getAsFloat
    }

    def getBigDecimal(property: String): BigDecimal = {
      json.get(property).getAsBigDecimal
    }

    def getInt(property: String): Int = {
      json.get(property).getAsInt
    }

    def getStringList(property: String): List[String] = {
      json.getAsJsonArray(property).asScala.map(_.getAsString).toList
    }

    def getObjectList(property: String): List[JsonObject] = {
      json.getAsJsonArray(property).asScala.map(_.getAsJsonObject).toList
    }

    def getBigDecimalList(property: String): List[BigDecimal] = {
      json.getAsJsonArray(property).asScala.map(_.getAsBigDecimal).map(BigDecimal(_)).toList
    }
  }

}
