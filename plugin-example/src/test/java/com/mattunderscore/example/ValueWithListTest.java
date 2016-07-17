package com.mattunderscore.example;

import com.example.PersonValue;
import com.example.ValueWithList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Matt Champion on 17/07/16
 */
public final class ValueWithListTest {
    @Test
    public void test() {
        final List<String> list = Arrays.asList("a", "b");
        final ValueWithList value = new ValueWithList(list);
        assertEquals(list, value.getNames());
        assertEquals("ValueWithList[names=[a, b]]", value.toString());
    }

    @Test
    public void equality() {
        final List<String> list = Arrays.asList("a", "b");
        final ValueWithList value0 = new ValueWithList(list);
        final ValueWithList value1 = new ValueWithList(list);

        assertTrue(value0.equals(value1));
        assertTrue(value1.equals(value0));
        assertEquals(value0.hashCode(), value1.hashCode());
    }

    @Test
    public void notEquals() {
        final List<String> list0 = Arrays.asList("a", "b");
        final List<String> list1 = Arrays.asList("a", "b", "c");
        final ValueWithList value0 = new ValueWithList(list0);
        final ValueWithList value1 = new ValueWithList(list1);

        assertFalse(value0.equals(value1));
        assertFalse(value1.equals(value0));
    }

    @Test
    public void notEqualsNull() {
        final List<String> list0 = Arrays.asList("a", "b");
        final ValueWithList value0 = new ValueWithList(list0);

        assertFalse(value0.equals(null));
    }

    @Test
    public void notEqualsObject() {
        final List<String> list0 = Arrays.asList("a", "b");
        final ValueWithList value0 = new ValueWithList(list0);

        assertFalse(value0.equals(new Object()));
    }
}