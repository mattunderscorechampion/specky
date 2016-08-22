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
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mattunderscore.specky.SemanticException;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.generator.scope.Scope;
import com.mattunderscore.specky.model.generator.scope.ScopeResolver;

/**
 * Generator for the model from the DSL model.
 * @author Matt Champion on 12/07/2016
 */
public final class ModelGenerator implements Supplier<SpecDesc> {
    private final List<DSLSpecDesc> dslSpecs;
    private final ScopeResolver scopeResolver;

    /**
     * Constructor.
     */
    public ModelGenerator(
            List<DSLSpecDesc> dslSpecs,
            ScopeResolver scopeResolver) {
        this.dslSpecs = dslSpecs;
        this.scopeResolver = scopeResolver;
    }

    @Override
    public SpecDesc get() {

        final List<AbstractTypeDesc> views = dslSpecs
            .stream()
            .map(this::getViews)
            .flatMap(Collection::stream)
            .collect(toList());

        final Map<String, AbstractTypeDesc> mappedViews = views
            .stream()
            .collect(toMap(viewDesc -> viewDesc.getPackageName() + "." + viewDesc.getName(), viewDesc -> viewDesc));
        final TypeDeriver typeDeriver = new TypeDeriver(scopeResolver, mappedViews);

        return SpecDesc
            .builder()
            .types(
                dslSpecs
                    .stream()
                    .map(dslSpecDesc -> getTypes(typeDeriver, dslSpecDesc))
                    .flatMap(Collection::stream)
                    .collect(toList()))
            .views(views)
            .build();
    }

    private List<ImplementationDesc> getTypes(TypeDeriver typeDeriver, DSLSpecDesc dslSpecDesc) {
        return dslSpecDesc
            .getTypes()
            .stream()
            .map(dslTypeDesc -> typeDeriver.deriveType(dslSpecDesc, dslTypeDesc))
            .collect(toList());
    }

    private List<AbstractTypeDesc> getViews(DSLSpecDesc dslSpecDesc) {
        return dslSpecDesc
            .getViews()
            .stream()
            .map(dslTypeDesc -> getView(dslSpecDesc, dslTypeDesc))
            .collect(toList());
    }

    private AbstractTypeDesc getView(DSLSpecDesc dslSpecDesc, DSLTypeDesc dslTypeDesc) {
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
