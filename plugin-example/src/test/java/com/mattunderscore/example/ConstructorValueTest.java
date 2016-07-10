package com.mattunderscore.example;

import static org.junit.Assert.assertEquals;
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
        final ConstructorValue person = new ConstructorValue(1, "Matt");

        assertEquals(1, person.getId());
        assertEquals("Matt", person.getName());
        assertEquals("ConstructorValue[id=1, name=Matt]", person.toString());
        assertTrue(person instanceof PersonType);
    }
}
