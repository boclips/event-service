package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.user.Address
import com.boclips.eventbus.domain.user.Deal
import com.boclips.eventbus.domain.user.Organisation
import java.time.ZonedDateTime
import java.util.Collections

object OrganisationFactory {

    fun createOrganisation(
        id: String = "organisation-id",
        name: String = "organisation-name",
        address: Address = createAddress(),
        deal: Deal = createDeal(),
        parent: Organisation? = null,
        type: String = "API",
        tags: Set<String> = emptySet(),
        features: Map<String, Boolean> = Collections.emptyMap()
    ): Organisation {
        return Organisation
            .builder()
            .id(id)
            .type(type)
            .name(name)
            .address(address)
            .deal(deal)
            .tags(tags)
            .features(features)
            .parent(parent)
            .build()
    }

    fun createAddress(
        countryCode: String? = null,
        state: String? = null,
        postCode: String? = null
    ): Address {
        return Address.builder()
            .countryCode(countryCode)
            .state(state)
            .postcode(postCode)
            .build()
    }

    fun createDeal(
        expiresAt: ZonedDateTime? = null,
        billing: Boolean = false
    ): Deal {
        return Deal.builder()
            .expiresAt(expiresAt)
            .billing(billing)
            .build()
    }
}
