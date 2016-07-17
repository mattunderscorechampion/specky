package com.mattunderscore.example;

import com.example.BeanWithList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link BeanWithList}.
 * @author Matt Champion on 17/07/16
 */
public final class BeanWithListTest {
    @Test
    public void test() {
        final List<String> list = Arrays.asList("a", "b");
        final BeanWithList value = new BeanWithList();
        assertEquals(new ArrayList<>(), value.getNames());
        value.setNames(list);
        assertEquals(list, value.getNames());
        assertEquals("BeanWithList[names=[a, b]]", value.toString());
    }

    @Test
    public void equality() {
        final List<String> list = Arrays.asList("a", "b");
        final BeanWithList value0 = new BeanWithList();
        final BeanWithList value1 = new BeanWithList();
        value0.setNames(list);
        value1.setNames(list);

        assertTrue(value0.equals(value1));
        assertTrue(value1.equals(value0));
        assertEquals(value0.hashCode(), value1.hashCode());
    }

    @Test
    public void notEquals() {
        final List<String> list0 = Arrays.asList("a", "b");
        final List<String> list1 = Arrays.asList("a", "b", "c");
        final BeanWithList value0 = new BeanWithList();
        final BeanWithList value1 = new BeanWithList();
        value0.setNames(list0);
        value1.setNames(list1);

        assertFalse(value0.equals(value1));
        assertFalse(value1.equals(value0));
    }

    @Test
    public void notEqualsNull() {
        final List<String> list0 = Arrays.asList("a", "b");
        final BeanWithList value0 = new BeanWithList();
        value0.setNames(list0);

        assertFalse(value0.equals(null));
    }

    @Test
    public void notEqualsObject() {
        final List<String> list0 = Arrays.asList("a", "b");
        final BeanWithList value0 = new BeanWithList();
        value0.setNames(list0);

        assertFalse(value0.equals(new Object()));
    }
}