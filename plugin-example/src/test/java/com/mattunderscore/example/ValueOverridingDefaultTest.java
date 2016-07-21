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

import com.example.ValueOverridingDefault;
import com.example.ValueWithExtraType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ValueWithExtraType}.
 * @author Matt Champion on 18/07/16
 */
public final class ValueOverridingDefaultTest {
    @Test
    public void test() {
        final ValueOverridingDefault value = ValueOverridingDefault.builder().build();

        assertEquals(10, value.getNumber());
        assertEquals("ValueOverridingDefault[number=10]", value.toString());
    }

    @Test
    public void equality() {
        final ValueOverridingDefault value0 = ValueOverridingDefault.builder().build();
        final ValueOverridingDefault value1 = ValueOverridingDefault.builder().build();

        assertTrue(value0.equals(value1));
        assertTrue(value1.equals(value0));
        assertEquals(value0.hashCode(), value1.hashCode());
    }

    @Test
    public void notEquals() {
        final ValueOverridingDefault value0 = ValueOverridingDefault.builder().build();
        final ValueOverridingDefault value1 = ValueOverridingDefault.builder().number(2).build();

        assertFalse(value0.equals(value1));
        assertFalse(value1.equals(value0));
    }

    @Test
    public void notEqualsNull() {
        final ValueOverridingDefault value0 = ValueOverridingDefault.builder().build();

        assertFalse(value0.equals(null));
    }

    @Test
    public void notEqualsObject() {
        final ValueOverridingDefault value0 = ValueOverridingDefault.builder().build();

        assertFalse(value0.equals(new Object()));
    }
}
