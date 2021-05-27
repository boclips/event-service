package com.boclips.event.infrastructure.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithAncestorsDocument {
    private String code;
    private String description;
    private Set<String> ancestors;

    public static CategoryWithAncestorsDocumentBuilder sample() {
        return CategoryWithAncestorsDocument.builder()
                .code("BZD")
                .description("Domestic Lizards")
                .ancestors(Collections.singleton("BZ"));
    }
}
