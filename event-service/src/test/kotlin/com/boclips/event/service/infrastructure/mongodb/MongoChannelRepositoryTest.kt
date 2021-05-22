package com.boclips.event.service.infrastructure.mongodb

import com.boclips.event.infrastructure.channel.ChannelDocument
import com.boclips.event.infrastructure.channel.DistributionMethodDocument
import com.boclips.event.service.domain.ChannelRepository
import com.boclips.event.service.testsupport.AbstractSpringIntegrationTest
import com.boclips.event.service.testsupport.ChannelFactory.createChannel
import com.boclips.event.service.testsupport.ChannelFactory.createChannelIngestDetails
import com.boclips.event.service.testsupport.ChannelFactory.createChannelMarketingDetails
import com.boclips.event.service.testsupport.ChannelFactory.createChannelPedagogyDetails
import com.boclips.event.service.testsupport.ChannelFactory.createChannelTopLevelDetails
import com.boclips.eventbus.domain.AgeRange
import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.contentpartner.DistributionMethod
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Period
import java.util.Locale

class MongoChannelRepositoryTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var channelRepository: ChannelRepository

    @Test
    fun `save and update a channel with only id and name`() {
        val id = "my-simple-channel"
        channelRepository.save(createChannel(id = id, name = "My simple channel!"))

        val created = getSingleDocument()
        assertThat(created.id).isEqualTo(id)
        assertThat(created.name).isEqualTo("My simple channel!")

        channelRepository.save(createChannel(id = id, name = "My new name"))

        val updated = getSingleDocument()
        assertThat(updated.id).isEqualTo(id)
        assertThat(updated.name).isEqualTo("My new name")
    }

    @Test
    fun `save channel with top-level details`() {
        val id = "top-level-channel"
        channelRepository.save(
            createChannel(
                id = id, details = createChannelTopLevelDetails(
                    contentTypes = listOf("NEWS", "INSTRUCTIONAL"),
                    contentCategories = listOf("Training", "Learning", "Animation"),
                    language = Locale.ITALIAN,
                    contractId = "contract-id",
                    notes = "just kidding"
                )
            )
        )

        val document = getSingleDocument()
        assertThat(document.id).isEqualTo(id)
        val details = document.details
        assertThat(details.contentTypes).containsExactlyInAnyOrder("NEWS", "INSTRUCTIONAL")
        assertThat(details.contentCategories).containsExactlyInAnyOrder("Training", "Learning", "Animation")
        assertThat(details.language).isEqualTo("it")
        assertThat(details.contractId).isEqualTo("contract-id")
        assertThat(details.notes).isEqualTo("just kidding")
    }

    @Test
    fun `save channel with ingest details`() {
        val id = "ingest-channel"
        channelRepository.save(
            createChannel(
                id = id, ingest = createChannelIngestDetails(
                    type = "MRSS",
                    deliveryFrequency = Period.ofMonths(2),
                    distributionMethods = setOf(DistributionMethod.STREAM, DistributionMethod.DOWNLOAD)
                )
            )
        )

        val document = getSingleDocument()
        assertThat(document.id).isEqualTo(id)
        val ingest = document.ingest
        assertThat(ingest.type).isEqualTo("MRSS")
        assertThat(ingest.deliveryFrequency).isEqualTo("P2M")
        assertThat(ingest.distributionMethods)
            .containsExactlyInAnyOrder(
                DistributionMethodDocument.STREAM,
                DistributionMethodDocument.DOWNLOAD
            )
    }

    @Test
    fun `save channel with pedagogy details`() {
        val id = "pedagogy-channel"
        channelRepository.save(
            createChannel(
                id = id, pedagogy = createChannelPedagogyDetails(
                    subjects = listOf(
                        Subject
                            .builder()
                            .id(SubjectId("subject-1"))
                            .name("subject 1")
                            .build(),
                        Subject
                            .builder()
                            .id(SubjectId("subject-2"))
                            .name("subject 2")
                            .build()
                    ),
                    ageRange = AgeRange.builder().min(8).max(16).build(),
                    bestForTags = listOf("tag1", "tag2", "cooltag")
                )
            )
        )

        val document = getSingleDocument()
        assertThat(document.id).isEqualTo(id)
        val pedagogy = document.pedagogy
        assertThat(pedagogy.subjectNames)
            .containsExactlyInAnyOrder("subject 1", "subject 2")
        assertThat(pedagogy.ageRangeMin).isEqualTo(8)
        assertThat(pedagogy.ageRangeMax).isEqualTo(16)
        assertThat(pedagogy.bestForTags).containsExactly("tag1", "tag2", "cooltag")
    }

    @Test
    fun `save channel with marketing details`() {
        val id = "marketing-channel"
        channelRepository.save(
            createChannel(
                id = id, marketing = createChannelMarketingDetails(
                    status = "My status",
                    oneLineIntro = "One line intro",
                    logos = listOf("http://website.one", "http://website.two"),
                    showreel = "http://movie.com/file.mov",
                    sampleVideos = listOf("http://one.com", "http://two.com", "http://three.com")
                )
            )
        )

        val document = getSingleDocument()
        assertThat(document.id)
        val marketing = document.marketing
        assertThat(marketing.status).isEqualTo("My status")
        assertThat(marketing.oneLineIntro).isEqualTo("One line intro")
        assertThat(marketing.logos).containsExactlyInAnyOrder(
            "http://website.one", "http://website.two"
        )
        assertThat(marketing.showreel).isEqualTo("http://movie.com/file.mov")
        assertThat(marketing.sampleVideos).containsExactlyInAnyOrder(
            "http://one.com", "http://two.com", "http://three.com"
        )
    }

    fun getSingleDocument() = document<ChannelDocument>(MongoChannelRepository.COLLECTION_NAME)
}
