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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link TypeResolver} for types in the standard Java library.
 *
 * @author Matt Champion on 06/06/16
 */
public final class JavaStandardTypeResolver implements TypeResolver {
    private final Map<String, String> nameToType = new HashMap<>();

    /**
     * Constructor.
     */
    public JavaStandardTypeResolver() {
        // Primitives
        nameToType.put("int", "int");
        nameToType.put("double", "double");
        nameToType.put("boolean", "boolean");
        nameToType.put("long", "long");

        // Boxed primitives
        nameToType.put("Integer", "java.lang.Integer");
        nameToType.put("java.lang.Integer", "java.lang.Integer");
        nameToType.put("Double", "java.lang.Double");
        nameToType.put("java.lang.Double", "java.lang.Double");
        nameToType.put("Boolean", "java.lang.Boolean");
        nameToType.put("java.lang.Boolean", "java.lang.Boolean");
        nameToType.put("Long", "java.lang.Long");
        nameToType.put("java.lang.Long", "java.lang.Long");

        // Big numbers
        nameToType.put("BigInteger", "java.math.BigInteger");
        nameToType.put("java.math.BigInteger", "java.math.BigInteger");
        nameToType.put("BigDecimal", "java.math.BigDecimal");
        nameToType.put("java.math.BigDecimal", "java.math.BigDecimal");

        // Simple classes
        nameToType.put("Object", "java.lang.Object");
        nameToType.put("java.lang.Object", "java.lang.Object");
        nameToType.put("String", "java.lang.String");
        nameToType.put("java.lang.String", "java.lang.String");

        // Generic classes
        nameToType.put("List", "java.util.List");
        nameToType.put("java.util.List", "java.util.List");
        nameToType.put("Set", "java.util.Set");
        nameToType.put("java.util.Set", "java.util.Set");
    }

    @Override
    public Optional<String> resolveType(String name) {
        return Optional.ofNullable(nameToType.get(name));
    }
}
