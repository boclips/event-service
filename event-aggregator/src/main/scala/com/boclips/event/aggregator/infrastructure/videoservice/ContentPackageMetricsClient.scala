package com.boclips.event.aggregator.infrastructure.videoservice

import com.boclips.event.aggregator.config.ContentPackageMetricsConfig
import com.boclips.event.aggregator.domain.model.Url
import com.boclips.event.aggregator.domain.model.contentpackages.ContentPackage
import com.boclips.event.aggregator.domain.model.videos.VideoId
import sttp.client3._
import sttp.client3.circe._
import io.circe.generic.auto._

case class TokenResponse(access_token: String)

case class VideoIdsResource(
                             _embedded: VideoIdsWrapperResource,
                             _links: Option[Map[String, HateoasLinkResource]]
                           )

case class HateoasLinkResource(
                                href: String,
                                templated: Boolean,
                              )

case class VideoIdsWrapperResource(
                                    videoIds: List[String]
                                  )

case class ContentPackageMetricsClientResult(
                                              videoIds: Seq[VideoId],
                                              cursor: Option[String]
                                            )

case class ContentPackageMetricsClient(config: ContentPackageMetricsConfig) {
  private val adminApi = s"${config.videoServiceUri}/v1/admin/actions"
  private val backend = HttpURLConnectionBackend()
  private val accessToken = basicRequest
    .body(
      Map(
        "grant_type" -> "client_credentials",
        "client_id" -> config.clientId,
        "client_secret" -> config.clientSecret
      )
    )
    .post(uri"${config.tokenUrl}/v1/token")
    .response(asJson[TokenResponse])
    .send(backend)
    .body
    .getOrElse(
      throw new Exception(
        "Could not acquire bearer token for content package metrics client"
      )
    )
    .access_token

  def getVideoIdsForContentPackage(
                                    contentPackage: ContentPackage,
                                    cursor: Option[String] = None
                                  ): ContentPackageMetricsClientResult = {
    val contentPackageId = contentPackage.id.value

    val uri = cursor match {
      case Some(c) =>
        uri"${adminApi}/videos_for_content_package/$contentPackageId?cursor=$c"
      case None =>
        uri"${adminApi}/videos_for_content_package/$contentPackageId"
    }

    val videosResponse = basicRequest
      .auth
      .bearer(accessToken)
      .get(uri)
      .response(asJson[VideoIdsResource])
      .send(backend)

    val body = videosResponse.body match {
      case Left(err) => throw new Exception(err)
      case Right(body) => body
    }

    val newCursor = body
      ._links
      .flatMap(_.get("next"))
      .map(_.href)
      .map(Url.parse)
      .flatMap(_.param("cursor"))

    ContentPackageMetricsClientResult(
      videoIds = body._embedded.videoIds.map(VideoId),
      cursor = newCursor
    )
  }
}
