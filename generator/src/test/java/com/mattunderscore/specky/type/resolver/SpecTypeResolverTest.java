package com.mattunderscore.specky.type.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
        final SpecTypeResolver firstResolver = new SpecTypeResolver("com.example").registerTypeName("Test");
        final SpecTypeResolver secondResolver = new SpecTypeResolver("com.example").registerTypeName("XTest");
        final TypeResolver resolver = firstResolver.merge(secondResolver);

        assertTrue(resolver.resolve("Test").isPresent());
        assertTrue(resolver.resolve("XTest").isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void failToMergeDifferentPackages() {
        final SpecTypeResolver firstResolver = new SpecTypeResolver("com.example").registerTypeName("Test");
        final SpecTypeResolver secondResolver = new SpecTypeResolver("com.example.other").registerTypeName("XTest");
        firstResolver.merge(secondResolver);
    }
}
