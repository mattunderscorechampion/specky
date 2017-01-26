/* Copyright © 2016-2017 Matthew Champion
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

import static com.mattunderscore.example.ReflectionAssertions.assertHasMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.example.BeanPersonType;
import com.example.PersonType;

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
