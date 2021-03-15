package com.boclips.event.infrastructure.user;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {
    @NonNull
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> subjects;
    private List<Integer> ages;
    @NonNull
    private String createdAt;
    private OrganisationDocument organisation;
    private OrganisationDocument profileSchool;
    private String role;
    @NonNull
    private Boolean boclipsEmployee;
    private Boolean hasOptedIntoMarketing;
    private String marketingUtmSource;
    private String marketingUtmTerm;
    private String marketingUtmCampaign;
    private String marketingUtmMedium;
    private String marketingUtmContent;
    private String externalId;

    public static UserDocument.UserDocumentBuilder sample() {
        return UserDocument.builder()
                .id("user-id")
                .firstName("my first name")
                .lastName("my last name")
                .email("my@email.com")
                .subjects(Collections.singletonList("SUBJECT1"))
                .ages(Collections.singletonList(5))
                .createdAt("2020-05-28T16:18:17.945+01:00")
                .organisation(OrganisationDocument.sample().build())
                .profileSchool(OrganisationDocument.sample().build())
                .role("my role")
                .boclipsEmployee(true)
                .hasOptedIntoMarketing(null)
                .marketingUtmSource("Of truth")
                .marketingUtmContent("surprise")
                .marketingUtmTerm("mine")
                .marketingUtmMedium("plasma")
                .marketingUtmCampaign("India")
                .externalId(null)
                ;
    }
}
