package com.mattunderscore.value.spec.type.resolver;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link SpecTypeResolver}.
 * @author Matt Champion on 08/06/16
 */
public class SpecTypeResolverTest {

    @Test
    public void resolve() {
        final TypeResolver resolver = new SpecTypeResolver("com.example").registerTypeName("Test");

        assertEquals("com.example.Test", resolver.resolve("Test").get());
    }

    @Test
    public void get() {
        final TypeResolver resolver = new SpecTypeResolver("com.example").registerTypeName("Test");

        assertEquals("com.example.Test", resolver.resolve("com.example.Test").get());
    }

    @Test
    public void unknown() {
        final TypeResolver resolver = new SpecTypeResolver("com.example").registerTypeName("Test");

        assertFalse(resolver.resolve("XTest").isPresent());
    }

    @Test
    public void merge() {
        final TypeResolver resolver = new SpecTypeResolver("com.example")
            .registerTypeName("Test")
            .merge(new SpecTypeResolver("com.example").registerTypeName("XTest"));

        assertTrue(resolver.resolve("XTest").isPresent());
    }
}
