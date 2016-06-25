package com.mattunderscore.specky.value.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Unit tests for {@link CompositeValueResolver}.
 *
 * @author Matt Champion on 25/06/2016
 */
public final class CompositeValueResolverTest {
    final CompositeValueResolver resolver = new CompositeValueResolver();

    @Test
    public void resolve() {
        assertFalse(resolver.resolve("java.lang.String").isPresent());
    }

    @Test
    public void with() {
        assertEquals("null", resolver.with(new NullValueResolver()).resolve("java.lang.String").get());
    }
}
