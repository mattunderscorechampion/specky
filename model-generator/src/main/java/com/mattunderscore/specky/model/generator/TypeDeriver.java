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

package com.mattunderscore.specky.model.generator;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.mattunderscore.specky.SemanticException;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.generator.scope.Scope;
import com.mattunderscore.specky.model.generator.scope.ScopeResolver;

/**
 * Fully derive a type from its superinterfaces.
 *
 * @author Matt Champion on 22/08/2016
 */
public final class TypeDeriver {
    private final ScopeResolver scopeResolver;

    /**
     * Constructor.
     */
    public TypeDeriver(ScopeResolver scopeResolver) {
        this.scopeResolver = scopeResolver;
    }

    /**
     * @return the fully derived type
     */
    public AbstractTypeDesc deriveType(DSLSpecDesc dslSpecDesc, DSLTypeDesc dslTypeDesc) {
        final Scope scope = scopeResolver.resolve(dslSpecDesc);

        final List<PropertyDesc> properties = dslTypeDesc
            .getProperties()
            .stream()
            .map(dslProperty -> getViewProperty(scope, dslProperty))
            .collect(toList());

        final LicenceResolver licenceResolver = scope.getLicenceResolver();

        return AbstractTypeDesc
            .builder()
            .licence(licenceResolver.resolve(dslTypeDesc.getLicence()).orElse(null))
            .author(dslSpecDesc.getAuthor())
            .packageName(dslSpecDesc.getPackageName())
            .name(dslTypeDesc.getName())
            .properties(properties)
            .supertypes(dslTypeDesc.getSupertypes())
            .description(dslTypeDesc.getDescription())
            .build();
    }

    private PropertyDesc getViewProperty(Scope scope, DSLPropertyDesc dslPropertyDesc) {
        final String resolvedType = scope.getPropertyTypeResolver().resolveOrThrow(dslPropertyDesc);
        return PropertyDesc
            .builder()
            .name(dslPropertyDesc.getName())
            .type(resolvedType)
            .typeParameters(dslPropertyDesc
                .getTypeParameters()
                .stream()
                .map(scope.getTypeResolver()::resolveOrThrow)
                .collect(toList()))
            .defaultValue(getDefaultValue(scope, dslPropertyDesc, resolvedType))
            .constraint(dslPropertyDesc.getConstraint())
            .optional(dslPropertyDesc.isOptional())
            .override(true)
            .description(dslPropertyDesc.getDescription())
            .build();
    }

    private String getDefaultValue(Scope scope, DSLPropertyDesc dslPropertyDesc, String resolvedType) {
        final String defaultValue = dslPropertyDesc.getDefaultValue();

        if (defaultValue != null) {
            return defaultValue;
        }

        final String typeDefaultValue = scope
            .getValueResolver()
            .resolve(dslPropertyDesc, resolvedType)
            .get();
        if (!dslPropertyDesc.isOptional() && "null".equals(typeDefaultValue)) {
            throw new SemanticException(
                "The property " + dslPropertyDesc.getName() + " is not optional but has no default type");
        }

        return typeDefaultValue;
    }
}
