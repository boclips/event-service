package com.boclips.event.aggregator.domain.model

import java.net.{URL, URLDecoder}
import java.nio.charset.StandardCharsets

case class Url(host: String, path: String, params: Map[String, String], rawParams: String) {

  def param(name: String): Option[String] = params.get(name)
}

object Url {

  def parse(url: String): Url = {
    try {
      val uri = new URL(url)
      val params = tryParseQueryParams(uri)
      Url(uri.getHost, uri.getPath, params, uri.getQuery)
    } catch {
      case e: Exception => {
        println(s"Error parsing url: $url")
        throw new Error(e)
      }
    }
  }

  def tryParseQueryParams(url: URL): Map[String, String] = {
    val query = url.getQuery
    if (query == null) {
      return Map()
    }

    val keyValuePairs: Array[(String, String)] = query.split('&').filter(!_.isEmpty).map(param => {
      val maybeTuple = param.split('=')
      val key = maybeTuple(0)
      val value = if (maybeTuple.length == 2) maybeTuple(1) else ""

      (key, URLDecoder.decode(value, StandardCharsets.UTF_8.name()))
    })

    keyValuePairs.foldLeft(Map[String, String]())(_ + _)
  }
}
