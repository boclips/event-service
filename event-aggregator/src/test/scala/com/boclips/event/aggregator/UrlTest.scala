package com.boclips.event.aggregator

import com.boclips.event.aggregator.domain.model.Url
import com.boclips.event.aggregator.testsupport.Test

class UrlTest extends Test {

  "parse" should "extract decoded params" in {
    val url = "http://example.com/x?a=1&b=2%203"

    val queryParams = Url.parse(url).params

    queryParams.size should be(2)
    queryParams.get("a") should be(Some("1"))
    queryParams.get("b") should be(Some("2 3"))
  }

  it should "return an empty map if there is no query params" in {
    Url.parse("http://example.com").params should be(Map())
  }

  it should "return an empty map if there is no query params, but a ?" in {
    Url.parse("http://example.com?").params should be(Map())
  }

  it should "return an empty map if there is no query params, but a ? and &" in {
    Url.parse("http://example.com?&").params should be(Map())
  }

  it should "return a None value for a param that has no value" in {
    Url.parse("http://example.com?k=").params should be(Map(("k", "")))
  }

  it should "extract the path" in {
    val url = "http://example.com/x/123?a=1&b=2%203"

    val path = Url.parse(url).path

    path shouldBe "/x/123"
  }

  it should "set the domain" in {
    val url = "http://example.com/bla"

    val domain = Url.parse(url).host

    domain shouldBe "example.com"
  }

  it should "set rawParams" in {
    val url = "http://example.com/x/123?a=1&b=2%203"

    val rawParams = Url.parse(url).rawParams

    rawParams shouldBe "a=1&b=2%203"
  }

  "param" should "return the param when exists" in {
    val url = Url.parse("http://example.com/x?a=1&b=2")

    url.param("b") shouldBe Some("2")
  }

  it should "return None when param does not exist" in {
    val url = Url.parse("http://example.com/x")

    url.param("myparam") shouldBe None
  }
}
