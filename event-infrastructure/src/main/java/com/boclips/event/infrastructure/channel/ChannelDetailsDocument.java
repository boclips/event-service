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
public class ChannelDetailsDocument {
    private List<String> contentTypes;
    private List<String> contentCategories;
    private String language;
    private String contractId;
    private String notes;

    public static ChannelDetailsDocumentBuilder sample() {
        return ChannelDetailsDocument.builder()
                .contentTypes(Collections.singletonList("STOCK"))
                .contentCategories(Collections.singletonList("CATEGORY"))
                .language("ca-FR")
                .contractId("contract-id")
                .notes("my notes");
    }
}
