package com.mattunderscore.specky.model.generator.scope;

import static com.mattunderscore.specky.model.generator.scope.PreambleScope.INSTANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Unit tests for {@link PreambleScope}.
 *
 * @author Matt Champion 13/02/2017
 */
public final class PreambleScopeTest {
    @Test
    public void resolveString() {
        assertEquals("java.lang.String", INSTANCE.resolveType("String").get());
    }

    @Test
    public void resolveInteger() {
        assertEquals("java.lang.Integer", INSTANCE.resolveType("Integer").get());
    }

    @Test
    public void resolveDouble() {
        assertEquals("java.lang.Double", INSTANCE.resolveType("Double").get());
    }

    @Test
    public void getString() {
        assertEquals("java.lang.String", INSTANCE.resolveType("java.lang.String").get());
    }

    @Test
    public void getInteger() {
        assertEquals("java.lang.Integer", INSTANCE.resolveType("java.lang.Integer").get());
    }

    @Test
    public void getDouble() {
        assertEquals("java.lang.Double", INSTANCE.resolveType("java.lang.Double").get());
    }

    @Test
    public void unknown() {
        assertFalse(INSTANCE.resolveType("java.lang.BigInteger").isPresent());
    }

    @Test
    public void resolveInt() {
        assertEquals("int", INSTANCE.resolveType("int").get());
    }

    @Test
    public void resolveOptionalInt() {
        assertEquals("java.lang.Integer", INSTANCE.resolveType("int", true).get());
    }

    @Test
    public void resolveLicence() throws Exception {
        assertFalse(INSTANCE.resolveLicence("").isPresent());
    }

    @Test
    public void resolveValue() throws Exception {
        assertFalse(INSTANCE.resolveValue("" ,false).isPresent());
    }

    @Test
    public void getAuthor() throws Exception {
        assertNull(INSTANCE.getAuthor());
    }

    @Test
    public void getPackage() throws Exception {
        assertNull(INSTANCE.getPackage());
    }
}