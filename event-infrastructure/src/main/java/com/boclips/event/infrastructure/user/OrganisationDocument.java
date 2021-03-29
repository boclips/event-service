package com.boclips.event.infrastructure.user;

import lombok.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationDocument {
    @NonNull
    private String id;
    @NonNull
    private String name;
    private OrganisationDocument parent;
    @NonNull
    private String type;
    @NonNull
    private List<String> tags;
    private String postcode;
    private String countryCode;
    private String state;
    private String dealExpiresAt;
    private Boolean billing;
    private Map<String, Boolean> features;

    public static OrganisationDocument.OrganisationDocumentBuilder sample() {
        return OrganisationDocument.builder()
                .id("organisation-id")
                .name("organisation name")
                .parent(null)
                .type("API")
                .tags(Collections.singletonList("tag"))
                .postcode("12345")
                .countryCode("US")
                .state("CA")
                .dealExpiresAt("2020-05-28T16:18:17.945+01:00")
                .features(new HashMap<>())
                .billing(true);
    }
}
