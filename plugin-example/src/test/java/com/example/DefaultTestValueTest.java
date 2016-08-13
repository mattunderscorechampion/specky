package com.example;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link DefaultTestValue}.
 * @author Matt Champion on 13/08/16
 */
public final class DefaultTestValueTest {
    @Test
    public void create() {
        final DefaultTestValue value = DefaultTestValue.builder().build();
        assertEquals(BigDecimal.ZERO, value.getReqBigDecimal());
        assertEquals(BigInteger.ZERO, value.getReqBigInt());
        assertEquals(Boolean.FALSE, value.getReqBoxedBoolean());
        assertEquals(0.0, value.getReqBoxedDouble(), 0.1);
        assertEquals(0, (int) value.getReqBoxedInt());
        assertEquals(0L, (long) value.getReqBoxedLong());
        assertEquals(0.0, value.getReqDouble(), 0.1);
        assertEquals(0, value.getReqInt());
        assertEquals(Collections.emptyList(), value.getReqList());
        assertEquals(0L, value.getReqLong());
        assertEquals(Collections.emptySet(), value.getReqSet());
        assertEquals("", value.getReqString());

        assertNull(value.getOptBigDecimal());
        assertNull(value.getOptBigInt());
        assertNull(value.getOptBoxedBoolean());
        assertNull(value.getOptBoxedDouble());
        assertNull(value.getOptBoxedInt());
        assertNull(value.getOptBoxedLong());
        assertNull(value.getOptDouble());
        assertNull(value.getOptInt());
        assertNull(value.getOptList());
        assertNull(value.getOptLong());
        assertNull(value.getOptSet());
        assertNull(value.getOptString());
    }
}
