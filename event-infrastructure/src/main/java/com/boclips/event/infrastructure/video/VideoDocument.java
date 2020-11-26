package com.boclips.event.infrastructure.video;

import lombok.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDocument implements Serializable {
    @NonNull
    private String id;
    @NonNull
    private String ingestedAt;
    private String releasedOn;
    @NonNull
    private String title;
    @NonNull
    private String channelId;
    @NonNull
    private String playbackProviderType;
    @NonNull
    private String playbackId;
    @NonNull
    private Set<String> subjects;
    private Integer ageRangeMin;
    private Integer ageRangeMax;
    @NonNull
    private Integer durationSeconds;
    private String type;
    private Integer originalWidth;
    private Integer originalHeight;
    private List<VideoAssetDocument> assets;
    @NonNull
    private Boolean promoted;
    @NonNull
    private List<String> keywords;
    @NonNull
    private List<VideoTopicDocument> topics;

    public static VideoDocumentBuilder sample() {
        return VideoDocument.builder()
                .id("video-id")
                .ingestedAt("2020-05-26T17:10:40+00:00")
                .releasedOn("2020-05-26")
                .title("video title")
                .channelId("channel id")
                .playbackProviderType("providertype")
                .playbackId("playbackId")
                .subjects(Collections.singleton("subject"))
                .ageRangeMin(5)
                .ageRangeMax(13)
                .durationSeconds(30)
                .type("videotype")
                .originalWidth(1920)
                .originalHeight(1080)
                .assets(Collections.singletonList(VideoAssetDocument.sample().build()))
                .promoted(false)
                .topics(Collections.singletonList(VideoTopicDocument.builder()
                        .name("topic")
                        .confidence(0.2)
                        .language(Locale.ENGLISH.toLanguageTag())
                        .parent(
                                VideoTopicDocument.builder()
                                        .name("parent-topic")
                                        .confidence(0.5)
                                        .language(Locale.FRENCH.toLanguageTag())
                                        .build()
                        )
                        .build()
                ))
                .keywords(Collections.singletonList("keyword"));
    }
}
