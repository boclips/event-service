package com.user;

import lombok.*;

import java.util.Collections;
import java.util.Set;

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
    private Set<String> tags;
    private String postcode;
    private String countryCode;
    private String state;
    private String dealExpiresAt;
    private Boolean billing;

    public static OrganisationDocument.OrganisationDocumentBuilder sample() {
        return OrganisationDocument.builder()
                .id("organisation-id")
                .name("organisation name")
                .parent(null)
                .type("type")
                .tags(Collections.singleton("tag"))
                .postcode("12345")
                .countryCode("US")
                .state("CA")
                .dealExpiresAt("2020-05-26T12:46:19+0000")
                .billing(true);
    }
}
