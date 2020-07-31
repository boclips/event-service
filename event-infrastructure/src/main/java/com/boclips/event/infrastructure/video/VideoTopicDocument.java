package com.boclips.event.infrastructure.video;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTopicDocument implements Serializable {
    @NonNull
    private String name;
    @NonNull
    Double confidence;
    @NonNull
    private String language;
    private VideoTopicDocument parent;
}
