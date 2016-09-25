package com.mattunderscore.example;

import com.example.ValueWithList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ValueWithList}.
 * @author Matt Champion on 17/07/16
 */
public final class ValueWithListTest {
    @Test
    public void test() {
        final List<String> list = Arrays.asList("a", "b");
        final ValueWithList value = ValueWithList.builder().names(list).build();
        assertEquals(list, value.getNames());
        assertEquals("ValueWithList[names=[a, b]]", value.toString());
    }

    @Test
    public void testListCopied() {
        final List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        final ValueWithList value = ValueWithList.builder().names(list).build();
        assertNotSame(list, value.getNames());
        assertEquals(list, value.getNames());
        list.add("c");
        assertEquals(2, value.getNames().size());
        assertEquals("ValueWithList[names=[a, b]]", value.toString());
    }

    @Test
    public void equality() {
        final List<String> list = Arrays.asList("a", "b");
        final ValueWithList value0 = ValueWithList.builder().names(list).build();
        final ValueWithList value1 = ValueWithList.builder().names(list).build();

        assertTrue(value0.equals(value1));
        assertTrue(value1.equals(value0));
        assertEquals(value0.hashCode(), value1.hashCode());
    }

    @Test
    public void notEquals() {
        final List<String> list0 = Arrays.asList("a", "b");
        final List<String> list1 = Arrays.asList("a", "b", "c");
        final ValueWithList value0 = ValueWithList.builder().names(list0).build();
        final ValueWithList value1 = ValueWithList.builder().names(list1).build();

        assertFalse(value0.equals(value1));
        assertFalse(value1.equals(value0));
    }

    @Test
    public void notEqualsNull() {
        final List<String> list0 = Arrays.asList("a", "b");
        final ValueWithList value0 = ValueWithList.builder().names(list0).build();

        assertFalse(value0.equals(null));
    }

    @Test
    public void notEqualsObject() {
        final List<String> list0 = Arrays.asList("a", "b");
        final ValueWithList value0 = ValueWithList.builder().names(list0).build();

        assertFalse(value0.equals(new Object()));
    }
}