package com.mattunderscore.specky.value.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.squareup.javapoet.CodeBlock;

/**
 * Unit tests for {@link OptionalValueResolver}.
 * @author Matt Champion on 13/08/16
 */
public class OptionalValueResolverTest {
    private final DefaultValueResolver resolver = new OptionalValueResolver();

    @Test
    public void optional() {
        final Optional<CodeBlock> value = resolver.resolveValue("none", true);
        assertTrue(value.isPresent());
        assertEquals(CodeBlock.of("null"), value.get());
    }

    @Test
    public void required() {
        final Optional<CodeBlock> value = resolver.resolveValue("none", false);
        assertFalse(value.isPresent());
    }
}
