package com.mattunderscore.example;

import com.example.BeanPersonType;
import com.example.BeanWithList;
import com.example.PersonBean;
import com.example.PersonType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mattunderscore.example.ReflectionAssertions.assertHasMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link BeanPersonType}.
 * @author Matt Champion on 17/07/16
 */
public final class BeanPersonTypeTest {
    @Test
    public void test() {
        final BeanPersonType person = new BeanPersonType();

        assertEquals(0, person.getId());
        assertEquals("", person.getName());
        assertEquals("BeanPersonType[id=0, name=]", person.toString());
        assertTrue(person instanceof PersonType);
        person.setId(2);
        person.setName("someName");
        assertEquals(2, person.getId());
        assertEquals("someName", person.getName());
        assertEquals("BeanPersonType[id=2, name=someName]", person.toString());
    }

    @Test
    public void testStructure() throws NoSuchMethodException {
        assertHasMethod(BeanPersonType.class, "getId", Integer.TYPE);
        assertHasMethod(BeanPersonType.class, "getName", String.class);
        assertHasMethod(BeanPersonType.class, "setId", Void.TYPE, Integer.TYPE);
        assertHasMethod(BeanPersonType.class, "setName", Void.TYPE, String.class);
    }

    @Test
    public void equality() {
        final BeanPersonType person0 = new BeanPersonType();
        final BeanPersonType person1 = new BeanPersonType();

        assertTrue(person0.equals(person1));
        assertTrue(person1.equals(person0));
        assertEquals(person0.hashCode(), person1.hashCode());
    }

    @Test
    public void notEquals() {
        final BeanPersonType person0 = new BeanPersonType();
        final BeanPersonType person1 = new BeanPersonType();
        person1.setId(4);

        assertFalse(person0.equals(person1));
        assertFalse(person1.equals(person0));
    }

    @Test
    public void notEqualsNull() {
        final BeanPersonType person0 = new BeanPersonType();

        assertFalse(person0.equals(null));
    }

    @Test
    public void notEqualsObject() {
        final BeanPersonType person0 = new BeanPersonType();

        assertFalse(person0.equals(new Object()));
    }
}
