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

package com.mattunderscore.specky.value.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.squareup.javapoet.CodeBlock;
import org.junit.Test;

/**
 * Unit tests for {@link MutableValueResolver}.
 *
 * @author Matt Champion on 30/07/2016
 */
public final class MutableValueResolverTest {

    private final DSLPropertyDesc noneProperty = DSLPropertyDesc.builder().type("none").build();
    private final DSLPropertyDesc someProperty = DSLPropertyDesc.builder().type("some").build();

    @Test
    public void resolveNone() {
        final MutableValueResolver resolver = new MutableValueResolver();

        final Optional<CodeBlock> none = resolver.resolve(noneProperty, "none");
        assertFalse(none.isPresent());
    }

    @Test
    public void registerAndResolve() {
        final MutableValueResolver resolver = new MutableValueResolver();

        final Optional<CodeBlock> some = resolver.register("some", CodeBlock.of("other")).resolve(someProperty, "some");
        assertTrue(some.isPresent());
        assertEquals(CodeBlock.of("other"), some.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerTwice() {
        final MutableValueResolver resolver = new MutableValueResolver();

        resolver.register("some", CodeBlock.of("other")).register("some", CodeBlock.of("again"));
    }

    @Test(expected = NullPointerException.class)
    public void registerNullValue() {
        final MutableValueResolver resolver = new MutableValueResolver();

        resolver.register("some", null);
    }
}
