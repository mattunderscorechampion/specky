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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.example.PersonTypeValue;

/**
 * Unit tests for {@link PersonTypeValue}.
 * @author Matt Champion on 25/06/2016
 */
public final class PersonTypeValueTest {
    @Test
    public void test() {
        final PersonTypeValue person = PersonTypeValue.builder().id(5).name("Matt").build();

        assertEquals(5, (int) person.getId());
        assertEquals("Matt", person.getName());
        assertEquals("PersonTypeValue[id=5, name=Matt]", person.toString());
    }

    @Test
    public void equality() {
        final PersonTypeValue person0 = PersonTypeValue.builder().id(5).name("Matt").build();
        final PersonTypeValue person1 = PersonTypeValue.builder().id(5).name("Matt").build();

        assertTrue(person0.equals(person1));
        assertTrue(person1.equals(person0));
        assertEquals(person0.hashCode(), person1.hashCode());
    }

    @Test
    public void notEquals() {
        final PersonTypeValue person0 = PersonTypeValue.builder().id(5).name("Matt").build();
        final PersonTypeValue person1 = PersonTypeValue.builder().id(6).name("Matt").build();

        assertFalse(person0.equals(person1));
        assertFalse(person1.equals(person0));
    }

    @Test
    public void notEqualsNull() {
        final PersonTypeValue person0 = PersonTypeValue.builder().id(5).name("Matt").build();

        assertFalse(person0.equals(null));
    }

    @Test
    public void notEqualsObject() {
        final PersonTypeValue person0 = PersonTypeValue.builder().id(5).name("Matt").build();

        assertFalse(person0.equals(new Object()));
    }

    @Test
    public void applyFunction() {
        final PersonTypeValue person = PersonTypeValue.builder().id(5).apply(builder -> builder.name("M")).build();

        assertEquals(5, (int) person.getId());
        assertEquals("M", person.getName());
        assertEquals("PersonTypeValue[id=5, name=M]", person.toString());
    }
}
