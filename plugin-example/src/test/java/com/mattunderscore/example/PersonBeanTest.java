/* Copyright © 2016 Matthew Champion
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of mattunderscore.com nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL MATTHEW CHAMPION BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.mattunderscore.example;

import com.example.PersonBean;
import com.example.PersonType;
import org.junit.Test;

import static com.mattunderscore.example.ReflectionAssertions.assertHasMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link PersonBean}.
 * @author Matt Champion on 25/06/2016
 */
public final class PersonBeanTest {
    @Test
    public void test() {
        final PersonBean person = new PersonBean();

        assertEquals(0, person.getId());
        assertEquals("", person.getName());
        assertEquals("PersonBean[id=0, name=, birthTimestamp=0]", person.toString());
        assertTrue(person instanceof PersonType);
        person.setId(2);
        person.setName("someName");
        person.setBirthTimestamp(50L);
        assertEquals(2, person.getId());
        assertEquals("someName", person.getName());
        assertEquals(50L, person.getBirthTimestamp());
        assertEquals("PersonBean[id=2, name=someName, birthTimestamp=50]", person.toString());
    }

    @Test
    public void testStructure() throws NoSuchMethodException {
        assertHasMethod(PersonBean.class, "getId", Integer.TYPE);
        assertHasMethod(PersonBean.class, "getName", String.class);
        assertHasMethod(PersonBean.class, "getBirthTimestamp", Long.TYPE);
        assertHasMethod(PersonBean.class, "setId", Void.TYPE, Integer.TYPE);
        assertHasMethod(PersonBean.class, "setName", Void.TYPE, String.class);
        assertHasMethod(PersonBean.class, "setBirthTimestamp", Void.TYPE, Long.TYPE);
    }

    @Test
    public void equality() {
        final PersonBean person0 = new PersonBean();
        final PersonBean person1 = new PersonBean();

        assertTrue(person0.equals(person1));
        assertTrue(person1.equals(person0));
        assertEquals(person0.hashCode(), person1.hashCode());
    }

    @Test
    public void notEquals() {
        final PersonBean person0 = new PersonBean();
        final PersonBean person1 = new PersonBean();
        person1.setId(4);

        assertFalse(person0.equals(person1));
        assertFalse(person1.equals(person0));
    }

    @Test
    public void notEqualsNull() {
        final PersonBean person0 = new PersonBean();

        assertFalse(person0.equals(null));
    }

    @Test
    public void notEqualsObject() {
        final PersonBean person0 = new PersonBean();

        assertFalse(person0.equals(new Object()));
    }
}
