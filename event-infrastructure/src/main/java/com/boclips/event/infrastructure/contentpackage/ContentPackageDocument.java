package com.boclips.event.infrastructure.contentpackage;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentPackageDocument {
    @NonNull
    String id;
    @NonNull
    String name;

    public static ContentPackageDocumentBuilder sample() {
        return ContentPackageDocument.builder()
                    .id("content-package-id")
                    .name("content package name");
    }
}
