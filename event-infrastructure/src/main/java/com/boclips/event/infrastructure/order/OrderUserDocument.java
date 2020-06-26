package com.boclips.event.infrastructure.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUserDocument {
    private String email;
    private String firstName;
    private String lastName;
    private String legacyUserId;
    private String label;

    public static OrderUserDocumentBuilder sample() {
        return OrderUserDocument.builder()
                .email("rob@co.com")
                .firstName("hop")
                .lastName("Curie")
                .legacyUserId("terry-2")
                .label("complete-user");
    }
}
