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

import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ViewDesc;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Generator for the model from the DSL model.
 * @author Matt Champion on 12/07/2016
 */
public final class ModelGenerator implements Supplier<SpecDesc> {
    private final List<DSLSpecDesc> dslSpecs;
    private final TypeResolver typeResolver;
    private final DefaultValueResolver valueResolver;

    public ModelGenerator(List<DSLSpecDesc> dslSpecs, TypeResolver typeResolver, DefaultValueResolver valueResolver) {
        this.dslSpecs = dslSpecs;
        this.typeResolver = typeResolver;
        this.valueResolver = valueResolver;
    }

    @Override
    public SpecDesc get() {

        final List<ViewDesc> views = dslSpecs
            .stream()
            .map(this::get)
            .flatMap(Collection::stream)
            .collect(toList());

        final Map<String, ViewDesc> mappedViews = getMappedViews(dslSpecs);
        final TypeDeriver typeDeriver = new TypeDeriver(typeResolver, valueResolver, mappedViews);

        return SpecDesc
            .builder()
            .values(
                dslSpecs
                    .stream()
                    .map(dslSpecDesc -> get(typeDeriver, dslSpecDesc))
                    .flatMap(Collection::stream)
                    .collect(toList()))
            .views(views)
            .build();
    }

    private Map<String, ViewDesc> getMappedViews(List<DSLSpecDesc> dslSpecs) {
        return dslSpecs
            .stream()
            .map(this::getMappedViews)
            .map(Map::entrySet)
            .flatMap(Collection::stream)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, ViewDesc> getMappedViews(DSLSpecDesc dslSpec) {
        return dslSpec
            .getViews()
            .stream()
            .collect(toMap(
                view -> dslSpec.getPackageName() + "." + view.getName(),
                view -> ViewDesc
                    .builder()
                    .packageName(dslSpec.getPackageName())
                    .name(view.getName())
                    .properties(view
                        .getProperties()
                        .stream()
                        .map(prop -> {
                            final String resolvedType = typeResolver.resolveOrThrow(prop.getTypeName());
                            return PropertyDesc
                                .builder()
                                .name(prop.getName())
                                .typeName(resolvedType)
                                .typeParameters(prop
                                    .getTypeParameters()
                                    .stream()
                                    .map(typeResolver::resolveOrThrow)
                                    .collect(toList()))
                                .override(true)
                                .defaultValue(valueResolver.resolve(resolvedType).get())
                                .build();
                        })
                        .collect(toList()))
                    .build()));
    }

    private List<TypeDesc> get(TypeDeriver typeDeriver, DSLSpecDesc dslSpecDesc) {
        final String packageName = dslSpecDesc.getPackageName();
        return dslSpecDesc
            .getValues()
            .stream()
            .map(dslTypeDesc -> typeDeriver.deriveType(packageName, dslTypeDesc))
            .collect(toList());
    }

    private List<ViewDesc> get(DSLSpecDesc dslSpecDesc) {
        final String packageName = dslSpecDesc.getPackageName();
        return dslSpecDesc
            .getViews()
            .stream()
            .map(dslTypeDesc -> get(packageName, dslTypeDesc))
            .collect(toList());
    }

    private ViewDesc get(String packageName, DSLViewDesc dslViewDesc) {
        final List<PropertyDesc> properties = dslViewDesc
            .getProperties()
            .stream()
            .map(this::get)
            .collect(toList());

        return ViewDesc
            .builder()
            .packageName(packageName)
            .name(dslViewDesc.getName())
            .properties(properties)
            .build();
    }

    private PropertyDesc get(DSLPropertyDesc dslPropertyDesc) {
        final String defaultValue = dslPropertyDesc.getDefaultValue();
        final String resolvedType = typeResolver.resolveOrThrow(dslPropertyDesc.getTypeName());
        return PropertyDesc
            .builder()
            .name(dslPropertyDesc.getName())
            .typeName(resolvedType)
            .typeParameters(dslPropertyDesc
                .getTypeParameters()
                .stream()
                .map(typeResolver::resolveOrThrow)
                .collect(toList()))
            .defaultValue(defaultValue == null ? valueResolver.resolve(resolvedType).get() : defaultValue)
            .optionalProperty(dslPropertyDesc.isOptionalProperty())
            .override(false)
            .build();
    }
}
