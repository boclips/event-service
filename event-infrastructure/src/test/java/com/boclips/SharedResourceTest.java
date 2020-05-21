package com.boclips;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SharedResourceTest {
    @Test
    public void testIt() {
        SharedResource it = new SharedResource(3);
        assertThat(it.getField()).isEqualTo(3);
    }
}
