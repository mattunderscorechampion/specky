package com.mattunderscore.value.spec.type.resolver;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link JavaStandardTypeResolver}.
 * @author Matt Champion on 08/06/16
 */
public class JavaStandardTypeResolverTest {
    private final JavaStandardTypeResolver resolver = new JavaStandardTypeResolver();

    @Test
    public void resolveString() {
        assertEquals("java.lang.String", resolver.resolve("String").get());
    }

    @Test
    public void resolveInteger() {
        assertEquals("java.lang.Integer", resolver.resolve("Integer").get());
    }

    @Test
    public void resolveDouble() {
        assertEquals("java.lang.Double", resolver.resolve("Double").get());
    }

    @Test
    public void getString() {
        assertEquals("java.lang.String", resolver.resolve("java.lang.String").get());
    }

    @Test
    public void getInteger() {
        assertEquals("java.lang.Integer", resolver.resolve("java.lang.Integer").get());
    }

    @Test
    public void getDouble() {
        assertEquals("java.lang.Double", resolver.resolve("java.lang.Double").get());
    }

    @Test
    public void unknown() {
        assertFalse(resolver.resolve("java.lang.BigInteger").isPresent());
    }
}
