package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.user.Organisation

object OrganisationFactory {

    fun createOrganisation(
        id: String = "organisation-id",
        accountType: String = "DESIGN_PARTNER",
        name: String = "organisation-name",
        postcode: String = "post-code",
        parent: Organisation? = null,
        type: String = "API"
    ): Organisation {
        return Organisation
            .builder()
            .id(id)
            .accountType(accountType)
            .type(type)
            .name(name)
            .postcode(postcode)
            .parent(parent)
            .build()
    }

}
