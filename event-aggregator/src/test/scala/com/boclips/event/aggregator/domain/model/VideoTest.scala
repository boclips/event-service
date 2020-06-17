package com.boclips.event.aggregator.domain.model

import java.time.{LocalDate, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.videos.Dimensions
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.{createVideo, createVideoAsset}

//noinspection ZeroIndexToHead
class VideoTest extends Test {

  val oneGbInKb = 1000 * 1000

  it should "compute monthly storage cost in GBP" in {
    val video = createVideo(assets = List(
      createVideoAsset(sizeKb = oneGbInKb),
      createVideoAsset(sizeKb = oneGbInKb),
      createVideoAsset(sizeKb = oneGbInKb),
    ))

    video.monthlyStorageCostGbp() shouldEqual (0.3 +- 0.00000001)
  }

  it should "compute storage cost so far in GBP" in {
    val video = createVideo(
      ingestedAt = ZonedDateTime.now().minusYears(2),
      assets = List(
        createVideoAsset(sizeKb = oneGbInKb),
        createVideoAsset(sizeKb = oneGbInKb),
      )
    )

    video.storageCostSoFarGbp() shouldEqual (4.8 +- 0.01)
  }

  "storage charges" should "create an entry for each month" in {
    val video = createVideo(
      ingestedAt = ZonedDateTime.parse("2020-02-15T12:00:00Z"),
      assets = createVideoAsset(sizeKb = oneGbInKb) :: Nil,
    )

    val charges = video.storageCharges(to = LocalDate.parse("2020-05-10"))
      .sortBy(_.periodStart.toEpochDay)

    charges should have length 4
    charges(0).periodStart shouldBe LocalDate.parse("2020-02-15")
    charges(0).periodEnd shouldBe LocalDate.parse("2020-02-29")
    charges(0).valueGbp shouldBe 0.05 +- 0.01
    charges(1).periodStart shouldBe LocalDate.parse("2020-03-01")
    charges(1).periodEnd shouldBe LocalDate.parse("2020-03-31")
    charges(1).valueGbp shouldBe 0.1 +- 0.01
    charges(2).periodStart shouldBe LocalDate.parse("2020-04-01")
    charges(2).periodEnd shouldBe LocalDate.parse("2020-04-30")
    charges(2).valueGbp shouldBe 0.1 +- 0.01
    charges(3).periodStart shouldBe LocalDate.parse("2020-05-01")
    charges(3).periodEnd shouldBe LocalDate.parse("2020-05-10")
    charges(3).valueGbp shouldBe 0.03 +- 0.01
  }

  it should "be empty for videos with no assets" in {
    val video = createVideo(
      assets = List()
    )

    val charges = video.storageCharges(to = LocalDate.now())

    charges shouldBe empty
  }

  "largest asset" should "give empty option when there is no assets" in {
    val video = createVideo(assets = List())

    video.largestAsset() shouldBe empty
  }

  it should "return the asset with largest size" in {
    val smallAsset = createVideoAsset(
      dimensions = Dimensions(1024, 720),
      sizeKb = 10000
    )

    val bigAsset = createVideoAsset(
      dimensions = Dimensions(1024, 720),
      sizeKb = 20000
    )

    val video = createVideo(assets = List(smallAsset, bigAsset))

    video.largestAsset() should contain(bigAsset)
  }

}
