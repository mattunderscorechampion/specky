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

import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.squareup.javapoet.CodeBlock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * Resolve the default value for standard Java types.
 * @author Matt Champion on 23/06/2016
 */
public final class JavaStandardDefaultValueResolver implements DefaultValueResolver {
    private final Map<String, CodeBlock> typeToDefault = new HashMap<>();

    /**
     * Constructor.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public JavaStandardDefaultValueResolver() {
        // Primitives
        typeToDefault.put("int", CodeBlock.of("$L", 0));
        typeToDefault.put("double", CodeBlock.of("$L", 0.0));
        typeToDefault.put("boolean", CodeBlock.of("$L", false));
        typeToDefault.put("long", CodeBlock.of("$LL", 0L));

        // Boxed primitives
        typeToDefault.put("java.lang.Integer", CodeBlock.of("$L", 0));
        typeToDefault.put("java.lang.Double", CodeBlock.of("$L", 0.0));
        typeToDefault.put("java.lang.Boolean", CodeBlock.of("$L", false));
        typeToDefault.put("java.lang.Long", CodeBlock.of("$LL", 0L));

        // Big numbers
        typeToDefault.put("java.math.BigInteger", CodeBlock.of("$T.ZERO", BigInteger.class));
        typeToDefault.put("java.math.BigDecimal", CodeBlock.of("$T.ZERO", BigDecimal.class));

        // Simple classes
        typeToDefault.put("java.lang.Object", CodeBlock.of("new $T()", Object.class));
        typeToDefault.put("java.lang.String", CodeBlock.of("$S", ""));

        // Generic classes
        typeToDefault.put("java.util.List", CodeBlock.of("new $T<>()", ArrayList.class));
        typeToDefault.put("java.util.Set", CodeBlock.of("new $T<>()", HashSet.class));
    }

    @Override
    public Optional<CodeBlock> resolve(DSLPropertyDesc propertyDesc, String resolvedType) {
        return Optional.ofNullable(typeToDefault.get(resolvedType));
    }

    @Override
    public Optional<CodeBlock> resolve(String resolvedType, boolean optional) {
        return Optional.ofNullable(typeToDefault.get(resolvedType));
    }
}
