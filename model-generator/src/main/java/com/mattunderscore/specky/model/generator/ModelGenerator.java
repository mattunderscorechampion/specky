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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mattunderscore.specky.dsl.model.DSLConstructionMethod;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.processed.model.BeanDesc;
import com.mattunderscore.specky.processed.model.ConstructionMethod;
import com.mattunderscore.specky.processed.model.PropertyImplementationDesc;
import com.mattunderscore.specky.processed.model.SpecDesc;
import com.mattunderscore.specky.processed.model.TypeDesc;
import com.mattunderscore.specky.processed.model.ValueDesc;
import com.mattunderscore.specky.processed.model.ViewDesc;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;

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

        return SpecDesc
            .builder()
            .values(
                dslSpecs
                    .stream()
                    .map(dslSpecDesc -> get(mappedViews, dslSpecDesc))
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
                            final String resolvedType = typeResolver.resolve(prop.getType()).get();
                            return PropertyImplementationDesc
                                .builder()
                                .name(prop.getName())
                                .type(resolvedType)
                                .override(true)
                                .defaultValue(valueResolver.resolve(resolvedType).get())
                                .build();
                        })
                        .collect(toList()))
                    .build()));
    }

    private List<TypeDesc> get(Map<String, ViewDesc> views, DSLSpecDesc dslSpecDesc) {
        final String packageName = dslSpecDesc.getPackageName();
        return dslSpecDesc
            .getValues()
            .stream()
            .map(dslViewDesc -> get(views, packageName, dslViewDesc))
            .collect(Collectors.toList());
    }

    private List<ViewDesc> get(DSLSpecDesc dslSpecDesc) {
        final String packageName = dslSpecDesc.getPackageName();
        return dslSpecDesc
            .getViews()
            .stream()
            .map(dslTypeDesc -> get(packageName, dslTypeDesc))
            .collect(Collectors.toList());
    }

    private TypeDesc get(Map<String, ViewDesc> views, String packageName, DSLTypeDesc dslTypeDesc) {
        for (String type : dslTypeDesc.getExtend()) {
            String resolvedType = typeResolver.resolve(type).get();
            final ViewDesc dslViewDesc = views.get(resolvedType);
            if (dslViewDesc == null) {
                throw new IllegalArgumentException("View not found for " + resolvedType + " in " + views);
            }
        }

        final List<PropertyImplementationDesc> inheritedProperties = dslTypeDesc
            .getExtend()
            .stream()
            .map(typeResolver::resolve)
            .map(Optional::get)
            .map(views::get)
            .map(ViewDesc::getProperties)
            .flatMap(Collection::stream)
            .collect(toList());

        final List<PropertyImplementationDesc> declaredProperties = dslTypeDesc
            .getProperties()
            .stream()
            .map(this::get)
            .collect(toList());

        final Map<String, PropertyImplementationDesc> knownProperties = new HashMap<>();
        final List<PropertyImplementationDesc> allProperties = new ArrayList<>();
        for (PropertyImplementationDesc property : inheritedProperties) {
            final String propertyName = property.getName();
            final PropertyImplementationDesc currentProperty = knownProperties.get(propertyName);
            if (currentProperty == null) {
                allProperties.add(property);
                knownProperties.put(propertyName, property);
            }
            else if (!property.getType().equals(currentProperty.getType())) {
                throw new IllegalArgumentException("Conflicting property declarations");
            }
        }
        for (PropertyImplementationDesc property : declaredProperties) {
            final String propertyName = property.getName();
            final PropertyImplementationDesc currentProperty = knownProperties.get(propertyName);
            if (currentProperty == null) {
                allProperties.add(property);
                knownProperties.put(propertyName, property);
            }
            else if (!property.getType().equals(currentProperty.getType())) {
                throw new IllegalArgumentException("Conflicting property declarations");
            }
        }

        if (dslTypeDesc instanceof DSLValueDesc) {
            return ValueDesc
                .builder()
                .packageName(packageName)
                .name(dslTypeDesc.getName())
                .constructionMethod(get(dslTypeDesc.getConstructionMethod()))
                .properties(allProperties)
                .extend(dslTypeDesc.getExtend())
                .build();
        }
        else {
            return BeanDesc
                .builder()
                .packageName(packageName)
                .name(dslTypeDesc.getName())
                .constructionMethod(get(dslTypeDesc.getConstructionMethod()))
                .properties(allProperties)
                .extend(dslTypeDesc.getExtend())
                .build();
        }
    }

    private ViewDesc get(String packageName, DSLViewDesc dslViewDesc) {
        final List<PropertyImplementationDesc> properties = dslViewDesc
            .getProperties()
            .stream()
            .map(this::get)
            .collect(Collectors.toList());

        return ViewDesc
            .builder()
            .packageName(packageName)
            .name(dslViewDesc.getName())
            .properties(properties)
            .build();
    }

    private ConstructionMethod get(DSLConstructionMethod method) {
        switch (method) {
            case CONSTRUCTOR:
                return ConstructionMethod.CONSTRUCTOR;
            case MUTABLE_BUILDER:
                return ConstructionMethod.MUTABLE_BUILDER;
            case IMMUTABLE_BUILDER:
                return ConstructionMethod.IMMUTABLE_BUILDER;
            default:
                throw new IllegalArgumentException("Unsupported construction method");
        }
    }

    private PropertyImplementationDesc get(DSLPropertyDesc dslPropertyDesc) {
        final String defaultValue = dslPropertyDesc.getDefaultValue();
        final String resolvedType = typeResolver.resolve(dslPropertyDesc.getType()).get();
        return PropertyImplementationDesc
            .builder()
            .name(dslPropertyDesc.getName())
            .type(resolvedType)
            .defaultValue(defaultValue == null ? valueResolver.resolve(resolvedType).get() : defaultValue)
            .optional(dslPropertyDesc.isOptional())
            .override(false)
            .build();
    }
}
