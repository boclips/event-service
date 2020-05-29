package com.boclips.event.infrastructure.collection;

import lombok.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDocument {
    @NonNull
    private String id;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @NonNull
    private List<String> subjects;
    private Integer minAge;
    private Integer maxAge;
    @NonNull
    private List<String> videoIds;
    @NonNull
    private String ownerId;
    @NonNull
    private List<String> bookmarks;
    @NonNull
    private Date createdTime;
    @NonNull
    private Date updatedTime;
    @NonNull
    private Boolean discoverable;
    @NonNull
    private Boolean deleted;

    public static CollectionDocumentBuilder sample() {
        return CollectionDocument.builder()
                .id("collection-id")
                .title("collection title")
                .description("collection description")
                .subjects(Collections.singletonList("subject"))
                .minAge(7)
                .maxAge(12)
                .videoIds(Collections.singletonList("video-id"))
                .ownerId("owner-id")
                .bookmarks(Collections.singletonList("bookmark"))
                .createdTime(new Date())
                .updatedTime(new Date())
                .discoverable(true)
                .deleted(false);
    }
}
