package com.mattunderscore.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.example.ConstructorValue;
import com.example.PersonType;

/**
 * Unit tests for {@link ConstructorValue}.
 *
 * @author Matt Champion on 10/07/2016
 */
public final class ConstructorValueTest {

    @Test
    public void test() {
        final ConstructorValue person = new ConstructorValue(1, "Matt", 1000);

        assertEquals(1, person.getId());
        assertEquals("Matt", person.getName());
        assertEquals(1000, person.getBirthTimestamp());
        assertEquals("ConstructorValue[id=1, name=Matt, birthTimestamp=1000]", person.toString());
        assertTrue(person instanceof PersonType);
    }

    @Test
    public void equality() {
        final ConstructorValue person0 = new ConstructorValue(1, "Matt", 1000);
        final ConstructorValue person1 = new ConstructorValue(1, "Matt", 1000);

        assertTrue(person0.equals(person1));
        assertTrue(person1.equals(person0));
        assertEquals(person0.hashCode(), person1.hashCode());
    }

    @Test
    public void notEquals() {
        final ConstructorValue person0 = new ConstructorValue(1, "Matt", 1000);
        final ConstructorValue person1 = new ConstructorValue(2, "Matt", 1000);

        assertFalse(person0.equals(person1));
        assertFalse(person1.equals(person0));
    }

    @Test
    public void notEqualsNull() {
        final ConstructorValue person0 = new ConstructorValue(1, "Matt", 1000);

        assertFalse(person0.equals(null));
    }

    @Test
    public void notEqualsObject() {
        final ConstructorValue person0 = new ConstructorValue(1, "Matt", 1000);

        assertFalse(person0.equals(new Object()));
    }
}
