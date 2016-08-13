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

import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import org.junit.Test;

/**
 * Unit tests for {@link JavaStandardDefaultValueResolver}.
 *
 * @author Matt Champion on 25/06/2016
 */
public final class JavaStandardDefaultValueResolverTest {

    private final DSLPropertyDesc stringProperty = DSLPropertyDesc.builder().type("java.lang.String").build();
    private final DSLPropertyDesc intProperty = DSLPropertyDesc.builder().type("int").build();
    private final DSLPropertyDesc doubleProperty = DSLPropertyDesc.builder().type("double").build();
    private final DSLPropertyDesc booleanProperty = DSLPropertyDesc.builder().type("boolean").build();
    private final DSLPropertyDesc boxedIntProperty = DSLPropertyDesc.builder().type("java.lang.Integer").build();
    private final DSLPropertyDesc boxedDoubleProperty = DSLPropertyDesc.builder().type("java.lang.Double").build();
    private final DSLPropertyDesc boxedBooleanProperty = DSLPropertyDesc.builder().type("java.lang.Boolean").build();

    private final JavaStandardDefaultValueResolver resolver = new JavaStandardDefaultValueResolver();

    @Test
    public void resolveBool() {
        assertEquals("false", resolver.resolve(booleanProperty, "boolean").get());
    }

    @Test
    public void resolveBoolean() {
        assertEquals("false", resolver.resolve(boxedBooleanProperty, "java.lang.Boolean").get());
    }

    @Test
    public void resolveInt() {
        assertEquals("0", resolver.resolve(intProperty, "int").get());
    }

    @Test
    public void resolveDbl() {
        assertEquals("0.0", resolver.resolve(doubleProperty, "double").get());
    }

    @Test
    public void getString() {
        assertEquals("\"\"", resolver.resolve(stringProperty, "java.lang.String").get());
    }

    @Test
    public void getInteger() {
        assertEquals("0", resolver.resolve(boxedIntProperty, "java.lang.Integer").get());
    }

    @Test
    public void getDouble() {
        assertEquals("0.0", resolver.resolve(boxedDoubleProperty, "java.lang.Double").get());
    }
}
