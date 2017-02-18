package com.mattunderscore.specky.model.generator.scope;

import static com.mattunderscore.specky.model.generator.scope.PreambleScope.INSTANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.squareup.javapoet.CodeBlock;

/**
 * Unit tests for {@link PreambleScope}.
 *
 * @author Matt Champion 13/02/2017
 */
public final class PreambleScopeTest {
    @Test
    public void resolveStringType() {
        assertEquals("java.lang.String", INSTANCE.resolveType("String").get());
    }

    @Test
    public void resolveIntegerType() {
        assertEquals("java.lang.Integer", INSTANCE.resolveType("Integer").get());
    }

    @Test
    public void resolveDoubleType() {
        assertEquals("java.lang.Double", INSTANCE.resolveType("Double").get());
    }

    @Test
    public void getStringType() {
        assertEquals("java.lang.String", INSTANCE.resolveType("java.lang.String").get());
    }

    @Test
    public void getIntegerType() {
        assertEquals("java.lang.Integer", INSTANCE.resolveType("java.lang.Integer").get());
    }

    @Test
    public void getDoubleType() {
        assertEquals("java.lang.Double", INSTANCE.resolveType("java.lang.Double").get());
    }

    @Test
    public void unknownType() {
        assertFalse(INSTANCE.resolveType("java.lang.BigInteger").isPresent());
    }

    @Test
    public void resolveIntType() {
        assertEquals("int", INSTANCE.resolveType("int").get());
    }

    @Test
    public void resolveOptionalIntType() {
        assertEquals("java.lang.Integer", INSTANCE.resolveType("int", true).get());
    }

    @Test
    public void resolveLicence() throws Exception {
        assertFalse(INSTANCE.resolveLicence("").isPresent());
    }

    @Test
    public void optional() {
        final Optional<CodeBlock> value = INSTANCE.resolveValue("none", true);
        assertTrue(value.isPresent());
        assertEquals(CodeBlock.of("null"), value.get());
    }

    @Test
    public void required() {
        final Optional<CodeBlock> value = INSTANCE.resolveValue("none", false);
        assertTrue(value.isPresent());
        assertEquals(CodeBlock.of("null"), value.get());
    }

    @Test
    public void getAuthor() throws Exception {
        assertNull(INSTANCE.getAuthor());
    }

    @Test
    public void getPackage() throws Exception {
        assertNull(INSTANCE.getPackage());
    }

    @Test
    public void resolveBool() {
        assertEquals(CodeBlock.of("$L", false), INSTANCE.resolveValue("boolean", false).get());
    }

    @Test
    public void resolveBoolean() {
        assertEquals(CodeBlock.of("$L", false), INSTANCE.resolveValue("java.lang.Boolean", false).get());
    }

    @Test
    public void resolveInt() {
        assertEquals(CodeBlock.of("$L", 0), INSTANCE.resolveValue("int", false).get());
    }

    @Test
    public void resolveDbl() {
        assertEquals(CodeBlock.of("$L", 0.0), INSTANCE.resolveValue("double", false).get());
    }

    @Test
    public void getString() {
        assertEquals(CodeBlock.of("$S", ""), INSTANCE.resolveValue("java.lang.String", false).get());
    }

    @Test
    public void getInteger() {
        assertEquals(CodeBlock.of("$L", 0), INSTANCE.resolveValue("java.lang.Integer", false).get());
    }

    @Test
    public void getDouble() {
        assertEquals(CodeBlock.of("$L", 0.0), INSTANCE.resolveValue("java.lang.Double", false).get());
    }
}
