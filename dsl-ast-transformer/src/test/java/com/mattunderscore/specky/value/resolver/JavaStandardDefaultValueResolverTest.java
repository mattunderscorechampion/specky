package com.mattunderscore.specky.value.resolver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link JavaStandardDefaultValueResolver}.
 *
 * @author Matt Champion on 25/06/2016
 */
public final class JavaStandardDefaultValueResolverTest {

    private final JavaStandardDefaultValueResolver resolver = new JavaStandardDefaultValueResolver();

    @Test
    public void resolveBool() {
        assertEquals("false", resolver.resolve("boolean").get());
    }

    @Test
    public void resolveBoolean() {
        assertEquals("false", resolver.resolve("java.lang.Boolean").get());
    }

    @Test
    public void resolveInt() {
        assertEquals("0", resolver.resolve("int").get());
    }

    @Test
    public void resolveDbl() {
        assertEquals("0.0", resolver.resolve("double").get());
    }

    @Test
    public void getString() {
        assertEquals("\"\"", resolver.resolve("java.lang.String").get());
    }

    @Test
    public void getInteger() {
        assertEquals("0", resolver.resolve("java.lang.Integer").get());
    }

    @Test
    public void getDouble() {
        assertEquals("0.0", resolver.resolve("java.lang.Double").get());
    }
}
