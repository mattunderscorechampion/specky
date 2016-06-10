package com.mattunderscore.value.spec.type.resolver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit tests for {@link CompositeTypeResolver}.
 * @author Matt Champion on 08/06/16
 */
public class CompositeTypeResolverTest {

    @Test
    public void resolveJava() throws Exception {
        final TypeResolver resolver = new CompositeTypeResolver()
                .registerResolver(new JavaStandardTypeResolver())
                .registerResolver(new SpecTypeResolver("com.example").registerTypeName("Test"));

        assertEquals("java.lang.String", resolver.resolve("String").get());
    }

    @Test
    public void resolveSpec() throws Exception {
        final TypeResolver resolver = new CompositeTypeResolver()
                .registerResolver(new JavaStandardTypeResolver())
                .registerResolver(new SpecTypeResolver("com.example").registerTypeName("Test"));

        assertEquals("com.example.Test", resolver.resolve("Test").get());
    }

    @Test
    public void unknown() {
        final TypeResolver resolver = new CompositeTypeResolver()
                .registerResolver(new JavaStandardTypeResolver())
                .registerResolver(new SpecTypeResolver("com.example").registerTypeName("Test"));

        assertFalse(resolver.resolve("XTest").isPresent());
    }
}
