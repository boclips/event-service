package com.boclips.event.aggregator.domain.service

import com.boclips.event.aggregator.domain.model.events.{Event, EventConstants}
import com.boclips.event.aggregator.domain.model.{API_ORGANISATION, SCHOOL_ORGANISATION, User, Video}
import com.boclips.event.aggregator.testsupport.IntegrationTest
import com.boclips.event.aggregator.testsupport.testfactories.VideoFactory.createVideo
import com.boclips.event.aggregator.testsupport.testfactories.{EventFactory, UserFactory}

class DataTest extends IntegrationTest {

  "teachersAppFilter" should "keep events of users with SCHOOL organisation" in sparkTest { implicit spark =>
    val events = rdd(EventFactory.createVideosSearchedEvent(userId = "schoolUserId"))
    val users = rdd(UserFactory.createUser(id = "schoolUserId", organisation = Some(UserFactory.createOrganisation(typeName = SCHOOL_ORGANISATION))))
    val videos = rdd[Video]()
    val data = Data(events, users, videos, "")

    val filteredData = data.schoolOnly()

    filteredData.events should not be empty
    filteredData.users should not be empty
  }

  it should "filter out events of users with API organisations" in sparkTest { implicit spark =>
    val events = rdd(EventFactory.createVideosSearchedEvent(userId = "apiUserId"))
    val users = rdd(UserFactory.createUser(id = "apiUserId", organisation = Some(UserFactory.createOrganisation(typeName = API_ORGANISATION))))
    val videos = rdd[Video]()
    val data = Data(events, users, videos, "")

    val filteredData = data.schoolOnly()

    filteredData.events shouldBe empty
    filteredData.users shouldBe empty
  }

  it should "keep events with no user organisation" in sparkTest { implicit spark =>
    val events = rdd(EventFactory.createVideosSearchedEvent(userId = "aUserId"))
    val users = rdd(UserFactory.createUser(id = "aUserId", organisation = None))
    val videos = rdd[Video]()
    val data = Data(events, users, videos, "")

    val filteredData = data.schoolOnly()

    filteredData.events should not be empty
    filteredData.users should not be empty
  }

  it should "keep events by anonymous users" in sparkTest { implicit spark =>
    val events = rdd(EventFactory.createVideoSegmentPlayedEvent(userId = EventConstants.anonymousUserId.value))
    val users = rdd[User]()
    val videos = rdd[Video]()
    val data = Data(events, users, videos, "")

    val filteredData = data.schoolOnly()

    filteredData.events should not be empty
  }

  it should "keep all videos" in sparkTest { implicit spark =>
    val events = rdd[Event]()
    val users = rdd[User]()
    val videos = rdd(createVideo())
    val data = Data(events, users, videos, "")

    val filteredData = data.schoolOnly()

    filteredData.videos should not be empty
  }

}
