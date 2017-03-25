package com.mattunderscore.specky.value.resolver;

import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
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
    private final DefaultValueResolver resolver = new OptionalValueResolver();

    @Test
    public void optional() {
        final Optional<LiteralDesc> value = resolver.resolveValue("none", true);
        assertTrue(value.isPresent());
        assertEquals(UnstructuredLiteral.builder().literal("null").build(), value.get());
    }

    @Test
    public void required() {
        final Optional<LiteralDesc> value = resolver.resolveValue("none", false);
        assertFalse(value.isPresent());
    }
}
