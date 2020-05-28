package com.boclips.event.infrastructure.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPedagogyDocument {
    private List<String> subjectNames;
    private Integer ageRangeMin;
    private Integer ageRangeMax;
    private List<String> bestForTags;
    private String curriculumAligned;
    private String educationalResources;
    private Boolean transcriptProvided;

    public static ChannelPedagogyDocumentBuilder sample() {
        return ChannelPedagogyDocument.builder()
                .subjectNames(Collections.singletonList("Math"))
                .ageRangeMin(5)
                .ageRangeMax(16)
                .bestForTags(Collections.singletonList("Kids"))
                .curriculumAligned("Cool curriculum")
                .educationalResources("Educational resource")
                .transcriptProvided(true);
    }
}
