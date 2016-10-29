package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * Unit tests for {@link WithMod}.
 *
 * @author Matt Champion on 30/10/2016
 */
public final class WithModTest {

    @Test
    public void createFrom() {
        final WithMod value = WithMod
            .builder()
            .id(5)
            .name("Matt")
            .build();

        assertEquals(5, value.getId());
        assertEquals("Matt", value.getName());

        final WithMod newValue = value
            .withId(6)
            .withName("Matt C");

        assertNotSame(value, newValue);

        assertEquals(5, value.getId());
        assertEquals("Matt", value.getName());
        assertEquals(6, newValue.getId());
        assertEquals("Matt C", newValue.getName());
    }
}
