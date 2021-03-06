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

package com.mattunderscore.specky.type.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

/**
 * Unit tests for {@link SpecTypeResolver}.
 * @author Matt Champion on 08/06/16
 */
public class SpecTypeResolverTest {

    @Test
    public void resolve() {
        final MutableTypeResolver resolver = new SpecTypeResolver();
        resolver.registerTypeName("com.example", "Test");

        assertEquals("com.example.Test", resolver.resolveType("Test").get());
    }

    @Test
    public void get() {
        final MutableTypeResolver resolver = new SpecTypeResolver();
        resolver.registerTypeName("com.example", "Test");

        assertEquals("com.example.Test", resolver.resolveType("com.example.Test").get());
    }

    @Test
    public void unknown() {
        final MutableTypeResolver resolver = new SpecTypeResolver();
        resolver.registerTypeName("com.example", "Test");

        assertFalse(resolver.resolveType("XTest").isPresent());
    }

    @Test
    public void registerDuplicate() {
        final MutableTypeResolver resolver = new SpecTypeResolver();
        final CompletableFuture<Void> res0 = resolver.registerTypeName("com.example", "Test");
        final CompletableFuture<Void> res1 = resolver.registerTypeName("com.example", "Test");

        assertTrue(res0.isDone());
        assertFalse(res0.isCompletedExceptionally());
        assertTrue(res1.isDone());
        assertTrue(res1.isCompletedExceptionally());
    }
}
