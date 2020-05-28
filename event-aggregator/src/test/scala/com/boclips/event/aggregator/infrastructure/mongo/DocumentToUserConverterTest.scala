package com.boclips.event.aggregator.infrastructure.mongo

import java.time.{ZoneOffset, ZonedDateTime}

import com.boclips.event.aggregator.domain.model.{API_ORGANISATION, UserId}
import com.boclips.event.aggregator.testsupport.Test
import com.boclips.event.aggregator.testsupport.testfactories.UserFactory.{createOrganisationDocument, createUserDocument}

class DocumentToUserConverterTest extends Test {

  it should "convert the id" in {
    val document = createUserDocument(id = "the id")

    val user = DocumentToUserConverter convert document

    user.id shouldBe UserId("the id")
  }

  it should "convert createdAt" in {
    val document = createUserDocument(createdAt = "2018-09-11T13:49:06.368Z")

    val user = DocumentToUserConverter convert document

    user.createdAt shouldBe ZonedDateTime.of(2018, 9, 11, 13, 49, 6, 368000000, ZoneOffset.UTC)
  }

  it should "convert isBoclipsEmployee when present" in {
    val document1 = createUserDocument(isBoclipsEmployee = true, boclipsEmployee = null)
    val document2 = createUserDocument(isBoclipsEmployee = false, boclipsEmployee = null)

    val user1 = DocumentToUserConverter convert document1
    val user2 = DocumentToUserConverter convert document2

    user1.isBoclipsEmployee shouldBe true
    user2.isBoclipsEmployee shouldBe false
  }

  it should "convert boclipsEmployee when present" in {
    val document1 = createUserDocument(isBoclipsEmployee = null, boclipsEmployee = true)
    val document2 = createUserDocument(isBoclipsEmployee = null, boclipsEmployee = false)

    val user1 = DocumentToUserConverter convert document1
    val user2 = DocumentToUserConverter convert document2

    user1.isBoclipsEmployee shouldBe true
    user2.isBoclipsEmployee shouldBe false
  }

  it should "convert organisation when null" in {
    val document = createUserDocument(organisation = None)

    val user = DocumentToUserConverter convert document

    user.organisation shouldBe None
  }

  it should "convert organisation when not null" in {
    val document = createUserDocument(organisation = Some(createOrganisationDocument(
      name = "the name",
      typeName = "API",
    )))

    val user = DocumentToUserConverter convert document

    user.organisation should not be empty
    user.organisation.get.name shouldBe "the name"
    user.organisation.get.`type` shouldBe API_ORGANISATION
  }

  it should "convert organisation parent when not null" in {
    val document = createUserDocument(organisation = Some(createOrganisationDocument(
      parent = Some(createOrganisationDocument(name = "parent organisation name"))
    )))

    val user = DocumentToUserConverter convert document

    user.organisation should not be empty
    user.organisation.get.parent should not be empty
    user.organisation.get.parent.get.name shouldBe "parent organisation name"
  }

  it should "convert organisation postcode when present" in {
    val userDocument = createUserDocument(organisation = Some(createOrganisationDocument(
      postcode = Some("SW115PF"))))

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.postcode shouldBe Some("SW115PF")
  }

  it should "handle null organisation postcode" in {
    val userDocument = createUserDocument(
      organisation = Some(createOrganisationDocument(
        postcode = None
      ))
    )

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.postcode shouldBe None
  }

  it should "convert state and countryCode" in {
    val userDocument = createUserDocument(
      organisation = Some(createOrganisationDocument(
        state = Some("OUR"),
        countryCode = Some("GZ")
      ))
    )

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.state shouldBe Some("OUR")
    user.organisation.get.countryCode shouldBe Some("GZ")
  }

  it should "convert organisation tags" in {
    val userDocument = createUserDocument(
      organisation = Some(createOrganisationDocument(
        tags = Some(Set("tag"))
      ))
    )

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.tags should contain only ("tag")
  }

  it should "handle organisation billing when populated" in {
    val userDocument = createUserDocument(
      organisation = Some(createOrganisationDocument(
        billing = Some(true)
      ))
    )

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.deal.billing shouldBe true
  }

  it should "default organisation billing to false when not specified" in {
    val userDocument = createUserDocument(
      organisation = Some(createOrganisationDocument(
        billing = None
      ))
    )

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.deal.billing shouldBe false
  }

  it should "handle null state and countryCode" in {
    val userDocument = createUserDocument(
      organisation = Some(createOrganisationDocument(
        state = None,
        countryCode = None
      ))
    )

    val user = DocumentToUserConverter convert userDocument

    user.organisation.get.state shouldBe None
    user.organisation.get.countryCode shouldBe None
  }

  it should "convert user details" in {
    val document = createUserDocument(firstName = Some("Bob"), lastName = Some("Bobson"), email = Some("bob@email.com"), role = Some("TEACHER"))

    val user = DocumentToUserConverter convert document

    user.firstName shouldBe Some("Bob")
    user.lastName shouldBe Some("Bobson")
    user.email shouldBe Some("bob@email.com")
    user.role shouldBe Some("TEACHER")
  }

  it should "convert subjects" in {
    val document = createUserDocument(subjects = Some(List("Bargaining 101", "Getting Space in the tube")))

    val user = DocumentToUserConverter convert document

    user.subjects shouldBe List("Bargaining 101", "Getting Space in the tube")
  }

  it should "convert handle null subjects" in {
    val document = createUserDocument(subjects = None)

    val user = DocumentToUserConverter convert document

    user.subjects shouldBe List()
  }

  it should "convert ages" in {
    val document = createUserDocument(ages = Some(List(7, 8)))

    val user = DocumentToUserConverter convert document

    user.ages shouldBe List(7, 8)
  }

  it should "convert handle null ages" in {
    val document = createUserDocument(ages = None)

    val user = DocumentToUserConverter convert document

    user.ages shouldBe List()
  }

}
