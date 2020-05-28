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
    private String _id;
    private String firstName;
    private String lastName;
    private String email;
    @NonNull
    private List<String> subjects;
    @NonNull
    private List<Integer> ages;
    @NonNull
    private String createdAt;
    private OrganisationDocument organisation;
    private OrganisationDocument profileSchool;
    private String role;
    @NonNull
    private Boolean isBoclipsEmployee;

    public static UserDocument.UserDocumentBuilder sample() {
        return UserDocument.builder()
                ._id("user-id")
                .firstName("my first name")
                .lastName("my last name")
                .email("my@email.com")
                .subjects(Collections.singletonList("SUBJECT1"))
                .ages(Collections.singletonList(5))
                .createdAt("2020-05-26T12:46:19+0000")
                .organisation(OrganisationDocument.sample().build())
                .profileSchool(OrganisationDocument.sample().build())
                .role("my role")
                .isBoclipsEmployee(true);
    }
}
