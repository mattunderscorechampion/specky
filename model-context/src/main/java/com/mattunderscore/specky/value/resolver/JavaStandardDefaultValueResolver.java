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

import com.mattunderscore.specky.literal.model.ComplexLiteral;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.literal.model.RealLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Resolve the default value for standard Java types.
 * @author Matt Champion on 23/06/2016
 */
public final class JavaStandardDefaultValueResolver implements DefaultValueResolver {
    private final Map<String, LiteralDesc> typeToDefault = new HashMap<>();

    /**
     * Constructor.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public JavaStandardDefaultValueResolver() {
        // Primitives
        typeToDefault.put("int", IntegerLiteral.builder().integerLiteral("0").build());
        typeToDefault.put("double", RealLiteral.builder().realLiteral("0.0").build());
        typeToDefault.put("boolean", UnstructuredLiteral.builder().literal("false").build());
        typeToDefault.put("long", IntegerLiteral.builder().integerLiteral("0L").build());

        // Boxed primitives
        typeToDefault.put("java.lang.Integer", IntegerLiteral.builder().integerLiteral("0").build());
        typeToDefault.put("java.lang.Double", RealLiteral.builder().realLiteral("0.0").build());
        typeToDefault.put("java.lang.Boolean", UnstructuredLiteral.builder().literal("false").build());
        typeToDefault.put("java.lang.Long", IntegerLiteral.builder().integerLiteral("0L").build());

        // Big numbers
        typeToDefault.put("java.math.BigInteger", UnstructuredLiteral.builder().literal("BigInteger.ZERO").build());
        typeToDefault.put("java.math.BigDecimal", UnstructuredLiteral.builder().literal("BigDecimal.ZERO").build());

        // Simple classes
        typeToDefault.put("java.lang.Object", ComplexLiteral.builder().typeName("Object").build());
        typeToDefault.put("java.lang.String", StringLiteral.builder().stringLiteral("").build());

        // Generic classes
        typeToDefault.put("java.util.List", ComplexLiteral.builder().typeName("java.util.ArrayList").build());
        typeToDefault.put("java.util.Set", ComplexLiteral.builder().typeName("java.util.HashSet").build());
    }

    @Override
    public Optional<LiteralDesc> resolveValue(String resolvedType, boolean optional) {
        return Optional.ofNullable(typeToDefault.get(resolvedType));
    }
}
