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

package com.mattunderscore.specky.type.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.mattunderscore.specky.SemanticErrorListener;

/**
 * Unit tests for {@link JavaStandardTypeResolver}.
 * @author Matt Champion on 08/06/16
 */
public class JavaStandardTypeResolverTest {

    private final JavaStandardTypeResolver resolver = new JavaStandardTypeResolver();

    @Test
    public void resolveString() {
        assertEquals("java.lang.String", resolver.resolve("String").get());
    }

    @Test
    public void resolveInteger() {
        assertEquals("java.lang.Integer", resolver.resolve("Integer").get());
    }

    @Test
    public void resolveDouble() {
        assertEquals("java.lang.Double", resolver.resolve("Double").get());
    }

    @Test
    public void getString() {
        assertEquals("java.lang.String", resolver.resolve("java.lang.String").get());
    }

    @Test
    public void getInteger() {
        assertEquals("java.lang.Integer", resolver.resolve("java.lang.Integer").get());
    }

    @Test
    public void getDouble() {
        assertEquals("java.lang.Double", resolver.resolve("java.lang.Double").get());
    }

    @Test
    public void unknown() {
        assertFalse(resolver.resolve("java.lang.BigInteger").isPresent());
    }
}
