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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mattunderscore.specky.dsl.model.DSLConstructionMethod;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.model.ViewDesc;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;

/**
 * Fully derive a type from its superinterfaces.
 *
 * @author Matt Champion on 21/07/16
 */
public final class TypeDeriver {
    private final TypeResolver typeResolver;
    private final DefaultValueResolver valueResolver;
    private final Map<String, ViewDesc> views;

    /**
     * Constructor.
     */
    public TypeDeriver(TypeResolver typeResolver, DefaultValueResolver valueResolver, Map<String, ViewDesc> views) {
        this.typeResolver = typeResolver;
        this.valueResolver = valueResolver;
        this.views = views;
    }

    /**
     * @return the fully derived type
     */
    public TypeDesc deriveType(DSLSpecDesc specDesc, DSLTypeDesc dslTypeDesc) {
        if (dslTypeDesc instanceof DSLValueDesc) {
            return ValueDesc
                .builder()
                .author(specDesc.getAuthor())
                .packageName(specDesc.getPackageName())
                .name(dslTypeDesc.getName())
                .constructionMethod(get(dslTypeDesc.getConstructionMethod()))
                .properties(deriveProperties(dslTypeDesc))
                .supertypes(dslTypeDesc.getSupertypes())
                .description(dslTypeDesc.getDescription())
                .build();
        }
        else {
            return BeanDesc
                .builder()
                .author(specDesc.getAuthor())
                .packageName(specDesc.getPackageName())
                .name(dslTypeDesc.getName())
                .constructionMethod(get(dslTypeDesc.getConstructionMethod()))
                .properties(deriveProperties(dslTypeDesc))
                .supertypes(dslTypeDesc.getSupertypes())
                .description(dslTypeDesc.getDescription())
                .build();
        }
    }

    private List<PropertyDesc> deriveProperties(DSLTypeDesc dslTypeDesc) {
        for (final String type : dslTypeDesc.getSupertypes()) {
            final String resolvedType = typeResolver.resolveOrThrow(type);
            final ViewDesc dslViewDesc = views.get(resolvedType);
            if (dslViewDesc == null) {
                throw new IllegalArgumentException("View not found for " + resolvedType + " in " + views);
            }
        }

        final List<PropertyDesc> inheritedProperties = dslTypeDesc
            .getSupertypes()
            .stream()
            .map(typeResolver::resolveOrThrow)
            .map(views::get)
            .map(ViewDesc::getProperties)
            .flatMap(Collection::stream)
            .collect(toList());

        final List<PropertyDesc> declaredProperties = dslTypeDesc
            .getProperties()
            .stream()
            .map(this::get)
            .collect(toList());

        final Map<String, PropertyDesc> knownProperties = new HashMap<>();
        final List<PropertyDesc> allProperties = new ArrayList<>();
        for (final PropertyDesc property : inheritedProperties) {
            final String propertyName = property.getName();
            final PropertyDesc currentProperty = knownProperties.get(propertyName);
            if (currentProperty == null) {
                allProperties.add(property);
                knownProperties.put(propertyName, property);
            }
            else {
                checkMegableProperties(currentProperty, property);
            }
        }
        for (final PropertyDesc property : declaredProperties) {
            final String propertyName = property.getName();
            final PropertyDesc currentProperty = knownProperties.get(propertyName);
            if (currentProperty == null) {
                allProperties.add(property);
                knownProperties.put(propertyName, property);
            }
            else {
                final PropertyDesc mergedProperty = mergeDeclaredProperty(currentProperty, property);
                allProperties.remove(currentProperty);
                allProperties.add(mergedProperty);
                knownProperties.put(propertyName, mergedProperty);
            }
        }

        return allProperties;
    }

    private void checkMegableProperties(PropertyDesc currentProperty, PropertyDesc newProperty) {
        if (!newProperty.getType().equals(currentProperty.getType())) {
            throw new IllegalArgumentException("Conflicting property declarations for " +
                currentProperty.getName() +
                ". Types " +
                currentProperty.getType() +
                " and " +
                newProperty.getType());
        }
        else if (newProperty.isOptional() != currentProperty.isOptional()) {
            throw new IllegalArgumentException("Conflicting property declarations for " +
                currentProperty.getName() +
                ". Cannot be both optional and required.");
        }
    }

    private PropertyDesc mergeDeclaredProperty(PropertyDesc currentProperty, PropertyDesc declaredProperty) {
        checkMegableProperties(currentProperty, declaredProperty);
        return declaredProperty;
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

    private PropertyDesc get(DSLPropertyDesc dslPropertyDesc) {
        final String defaultValue = dslPropertyDesc.getDefaultValue();
        final String resolvedType = typeResolver.resolveOrThrow(dslPropertyDesc.getType());
        return PropertyDesc
            .builder()
            .name(dslPropertyDesc.getName())
            .type(resolvedType)
            .typeParameters(dslPropertyDesc
                .getTypeParameters()
                .stream()
                .map(typeResolver::resolveOrThrow)
                .collect(toList()))
            .defaultValue(
                defaultValue == null && !dslPropertyDesc.isOptional() ?
                    valueResolver.resolve(resolvedType).get() :
                    defaultValue)
            .constraint(dslPropertyDesc.getConstraint())
            .optional(dslPropertyDesc.isOptional())
            .override(false)
            .description(dslPropertyDesc.getDescription())
            .build();
    }
}
