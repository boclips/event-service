package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}
import java.util

import com.boclips.event.aggregator.domain.model.users.{API_ORGANISATION, BoclipsUserIdentity, SCHOOL_ORGANISATION, UserId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.infrastructure.user.{OrganisationDocument, UserDocument}

import scala.collection.JavaConverters._

class DocumentToUserConverterTest extends Test {

  it should "convert the id" in {
    val document = UserDocument.sample
      .id("the id")
      .build()

    val user = DocumentToUserConverter convert document

    user.identity shouldBe BoclipsUserIdentity(UserId("the id"))
  }

  it should "convert createdAt" in {
    val document = UserDocument.sample
      .createdAt("2018-09-11T13:49:06.368Z")
      .build()

    val user = DocumentToUserConverter convert document

    user.createdAt shouldBe ZonedDateTime.of(2018, 9, 11, 13, 49, 6, 368000000, ZoneOffset.UTC)
  }

  it should "convert isBoclipsEmployee when present" in {
    val document1 = UserDocument.sample
      .boclipsEmployee(true)
      .build()
    val document2 = UserDocument.sample
      .boclipsEmployee(false)
      .build()

    val user1 = DocumentToUserConverter convert document1
    val user2 = DocumentToUserConverter convert document2

    user1.isBoclipsEmployee shouldBe true
    user2.isBoclipsEmployee shouldBe false
  }

  it should "convert hasOptedIntoMarketing when present" in {
    val userDocument1 = UserDocument.sample
      .hasOptedIntoMarketing(true)
      .build()

    val userDocument2 = UserDocument.sample
      .hasOptedIntoMarketing(false)
      .build()

    val user1 = DocumentToUserConverter convert userDocument1
    val user2 = DocumentToUserConverter convert userDocument2

    user1.hasOptedIntoMarketing shouldBe Some(true)
    user2.hasOptedIntoMarketing shouldBe Some(false)
  }

  it should "handle hasOptedIntoMarketing when not present" in {
    val userDocument1 = UserDocument.sample
      .hasOptedIntoMarketing(null)
      .build()

    val user1 = DocumentToUserConverter convert userDocument1

    user1.hasOptedIntoMarketing shouldBe Some(false)

  }

  it should "convert organisation when null" in {
    val document = UserDocument.sample
      .organisation(null)
      .build()

    val user = DocumentToUserConverter convert document

    user.organisation shouldBe None
  }

  it should "convert organisation when not null" in {
    val document = UserDocument.sample
      .organisation(
        OrganisationDocument.sample
          .name("the name")
          .`type`("API")
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert document

    user.organisation should not be empty
    user.organisation.get.name shouldBe "the name"
    user.organisation.get.`type` shouldBe API_ORGANISATION
  }

  it should "convert organisation parent when not null" in {
    val document = UserDocument.sample
      .organisation(
        OrganisationDocument.sample
          .parent(OrganisationDocument.sample
            .name("parent organisation name")
            .build()
          )
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert document

    user.organisation should not be empty
    user.organisation.get.parent should not be empty
    user.organisation.get.parent.get.name shouldBe "parent organisation name"
  }

  it should "convert organisation postcode when present" in {
    val userDocument = UserDocument.sample
      .organisation(
        OrganisationDocument.sample
          .postcode("SW115PF")
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.postcode shouldBe Some("SW115PF")
  }

  it should "handle null organisation postcode" in {
    val userDocument = UserDocument.sample
      .organisation(
        OrganisationDocument.sample
          .postcode(null)
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.postcode shouldBe None
  }

  it should "convert state and countryCode" in {
    val userDocument = UserDocument.sample
      .organisation(
        OrganisationDocument.sample
          .state("OUR")
          .countryCode("GZ")
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.state shouldBe Some("OUR")
    user.organisation.get.countryCode shouldBe Some("GZ")
  }

  it should "convert organisation tags" in {
    val userDocument = UserDocument.sample
      .organisation(
        OrganisationDocument.sample
          .tags(List("tag").asJava)
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.tags should contain only "tag"
  }

  it should "handle organisation billing when populated" in {
    val userDocument = UserDocument.sample
      .organisation(
        OrganisationDocument.sample
          .billing(true)
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.deal.billing shouldBe true
  }

  it should "default organisation billing to false when not specified" in {
    val userDocument = UserDocument.sample
      .organisation(
        OrganisationDocument
          .sample
          .billing(null)
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.deal.billing shouldBe false
  }

  it should "handle null state and countryCode" in {
    val userDocument = UserDocument.sample
      .organisation(
        OrganisationDocument.sample
          .state(null)
          .countryCode(null)
          .build()
      )
      .build()

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.state shouldBe None
    user.organisation.get.countryCode shouldBe None
  }

  it should "convert user details" in {
    val document = UserDocument.sample
      .firstName("Bob")
      .lastName("Bobson")
      .email("bob@email.com")
      .role("TEACHER")
      .build()

    val user = DocumentToUserConverter convert document

    user.firstName shouldBe Some("Bob")
    user.lastName shouldBe Some("Bobson")
    user.email shouldBe Some("bob@email.com")
    user.role shouldBe Some("TEACHER")
  }

  it should "convert subjects" in {
    val document = UserDocument.sample
      .subjects(List("Bargaining 101", "Getting Space in the tube").asJava)
      .build()

    val user = DocumentToUserConverter convert document

    user.subjects shouldBe List("Bargaining 101", "Getting Space in the tube")
  }

  it should "convert handle null subjects" in {
    val document = UserDocument.sample
      .subjects(null)
      .build()

    val user = DocumentToUserConverter convert document

    user.subjects shouldBe List()
  }

  it should "convert ages" in {
    val document = UserDocument.sample
      .ages(List(7, 8).map(_.asInstanceOf[Integer]).asJava)
      .build()

    val user = DocumentToUserConverter convert document

    user.ages shouldBe List(7, 8)
  }

  it should "convert handle null ages" in {
    val document = UserDocument.sample
      .ages(null)
      .build()

    val user = DocumentToUserConverter convert document

    user.ages shouldBe List()
  }

  it should "convert profile organisation when present" in {
    val document = UserDocument.sample
      .profileSchool(OrganisationDocument.sample
        .name("Colegio Concepcion Arenal")
        .countryCode("UAE")
        .postcode("32004")
        .state("GZ")
        .`type`("SCHOOL")
        .tags(util.Collections.singletonList("tag"))
        .parent(null)
        .dealExpiresAt("2022-05-29T16:18:17.945+01:00")
        .billing(false).build())
      .build()

    val user = DocumentToUserConverter convert document

    user.profileSchool.get.name shouldBe "Colegio Concepcion Arenal"
    user.profileSchool.flatMap(_.countryCode) shouldBe Some("UAE")
    user.profileSchool.flatMap(_.postcode) shouldBe Some("32004")
    user.profileSchool.flatMap(_.state) shouldBe Some("GZ")
    user.profileSchool.get.`type` shouldBe SCHOOL_ORGANISATION
    user.profileSchool.get.tags should contain only "tag"
    user.profileSchool.get.parent shouldBe None
    user.profileSchool.get.deal.billing shouldBe false
    user.profileSchool.get.deal.dealExpiresAt shouldBe Some(ZonedDateTime.of(2022, 5, 29, 16, 18, 17, 945000000, ZoneOffset.ofHours(1)))
  }

  it should "handle null Profile Organisation" in {
    val document = UserDocument.sample.profileSchool(null).build()

    val user = DocumentToUserConverter convert document

    user.profileSchool shouldBe None
  }

  it should "handle null profile organisation properties" in {
    val document = UserDocument.sample
      .profileSchool(OrganisationDocument.sample
        .countryCode(null)
        .postcode(null)
        .state(null)
        .parent(null)
        .dealExpiresAt(null).build())
      .build()

    val user = DocumentToUserConverter convert document

    user.profileSchool.flatMap(_.countryCode) shouldBe None
    user.profileSchool.flatMap(_.postcode) shouldBe None
    user.profileSchool.flatMap(_.state) shouldBe None
    user.profileSchool.get.parent shouldBe None
    user.profileSchool.get.deal.dealExpiresAt shouldBe None
  }

}
