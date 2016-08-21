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

import java.util.Optional;

import com.mattunderscore.specky.SemanticException;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;

/**
 * @author Matt Champion on 12/08/2016
 */
public final class PropertyTypeResolver {
    private final TypeResolver typeResolver;

    /**
     * Constructor.
     */
    public PropertyTypeResolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    /**
     * @param propertyDesc the type propertyDesc
     * @return optional fully qualified propertyDesc
     */
    public Optional<String> resolve(DSLPropertyDesc propertyDesc) {
        return typeResolver.resolve(propertyDesc.getType()).flatMap(type -> resolveOptional(type, propertyDesc.isOptional()));
    }

    private Optional<String> resolveOptional(String type, boolean optional) {
        if (!optional) {
            return Optional.of(type);
        }

        if ("int".equals(type)) {
            return Optional.of("java.lang.Integer");
        }
        else if ("long".equals(type)) {
            return Optional.of("java.lang.Long");
        }
        else if ("double".equals(type)) {
            return Optional.of("java.lang.Double");
        }
        else if ("boolean".equals(type)) {
            return Optional.of("java.lang.Boolean");
        }

        return Optional.of(type);
    }

    /**
     *
     * @param name the type name
     * @return fully qualified name
     * @throws SemanticException if the type name cannot be resolved
     */
    public String resolveOrThrow(DSLPropertyDesc name) {
        return resolve(name).orElseThrow(() -> new SemanticException("No resolvable type for " + name));
    }
}
