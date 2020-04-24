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
            .parent(parent)
                .state(state)
                .countryCode(countryCode)
            .build()
    }

}
