package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.user.Organisation

object OrganisationFactory {

    fun createOrganisation(
        id: String = "organisation-id",
        accountType: String = "DESIGN_PARTNER",
        name: String = "organisation-name",
        postcode: String = "post-code",
        parent: Organisation? = null,
        type: String = "API",
        tags: Set<String> = emptySet(),
        state: String? = null,
        countryCode: String? = null
    ): Organisation {
        return Organisation
            .builder()
            .id(id)
            .accountType(accountType)
            .type(type)
            .name(name)
            .postcode(postcode)
            .tags(tags)
            .parent(parent)
                .state(state)
                .countryCode(countryCode)
            .build()
    }

}
