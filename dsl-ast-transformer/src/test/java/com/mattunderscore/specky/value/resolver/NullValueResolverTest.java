package com.mattunderscore.specky.value.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Unit tests for {@link NullValueResolver}.
 *
 * @author Matt Champion on 25/06/2016
 */
public final class NullValueResolverTest {

    private final NullValueResolver resolver = new NullValueResolver();

    @Test
    public void objectsAreNull() {
        assertEquals("null", resolver.resolve("java.lang.String").get());
    }

    @Test
    public void primitivesUnresolved() {
        assertFalse(resolver.resolve("int").isPresent());
    }
}
