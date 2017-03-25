package com.mattunderscore.specky.model.generator.scope;

import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.literal.model.RealLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
import org.junit.Test;

import java.util.Optional;

import static com.mattunderscore.specky.model.generator.scope.PreambleScope.INSTANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
    public void resolveBSD3Licence() throws Exception {
        assertTrue(INSTANCE.resolveLicence("BSD-3-Clause").isPresent());
    }

    @Test
    public void resolveMITLicence() throws Exception {
        assertTrue(INSTANCE.resolveLicence("MIT").isPresent());
    }

    @Test
    public void optional() {
        final Optional<LiteralDesc> value = INSTANCE.resolveValue("none", true);
        assertTrue(value.isPresent());
        assertEquals(UnstructuredLiteral.builder().literal("null").build(), value.get());
    }

    @Test
    public void required() {
        final Optional<LiteralDesc> value = INSTANCE.resolveValue("none", false);
        assertTrue(value.isPresent());
        assertEquals(UnstructuredLiteral.builder().literal("null").build(), value.get());
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
        assertEquals(UnstructuredLiteral.builder().literal("false").build(), INSTANCE.resolveValue("boolean", false).get());
    }

    @Test
    public void resolveBoolean() {
        assertEquals(UnstructuredLiteral.builder().literal("false").build(), INSTANCE.resolveValue("java.lang.Boolean", false).get());
    }

    @Test
    public void resolveInt() {
        assertEquals(IntegerLiteral.builder().integerLiteral("0").build(), INSTANCE.resolveValue("int", false).get());
    }

    @Test
    public void resolveDbl() {
        assertEquals(RealLiteral.builder().realLiteral("0.0").build(), INSTANCE.resolveValue("double", false).get());
    }

    @Test
    public void getString() {
        assertEquals(StringLiteral.builder().stringLiteral("").build(), INSTANCE.resolveValue("java.lang.String", false).get());
    }

    @Test
    public void getInteger() {
        assertEquals(IntegerLiteral.builder().integerLiteral("0").build(), INSTANCE.resolveValue("java.lang.Integer", false).get());
    }

    @Test
    public void getDouble() {
        assertEquals(RealLiteral.builder().realLiteral("0.0").build(), INSTANCE.resolveValue("java.lang.Double", false).get());
    }

    @Test
    public void getCopyrightHolder() {
        assertNull(EmptyScope.INSTANCE.getCopyrightHolder());
    }
}
