package com.mattunderscore.specky.value.resolver;

import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.squareup.javapoet.CodeBlock;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link OptionalValueResolver}.
 * @author Matt Champion on 13/08/16
 */
public class OptionalValueResolverTest {
    private final DSLPropertyDesc requiredProperty = DSLPropertyDesc.builder().type("none").build();
    private final DSLPropertyDesc optionalProperty = DSLPropertyDesc.builder().optional(true).type("some").build();

    private final DefaultValueResolver resolver = new OptionalValueResolver();

    @Test
    public void optional() {
        final Optional<CodeBlock> value = resolver.resolve(optionalProperty, "none");
        assertTrue(value.isPresent());
        assertEquals(CodeBlock.of("null"), value.get());
    }

    @Test
    public void required() {
        final Optional<CodeBlock> value = resolver.resolve(requiredProperty, "none");
        assertFalse(value.isPresent());
    }

    @Test
    public void optionalDirect() {
        final Optional<CodeBlock> value = resolver.resolve("none", true);
        assertTrue(value.isPresent());
        assertEquals(CodeBlock.of("null"), value.get());
    }

    @Test
    public void requiredDirect() {
        final Optional<CodeBlock> value = resolver.resolve("none", false);
        assertFalse(value.isPresent());
    }
}
