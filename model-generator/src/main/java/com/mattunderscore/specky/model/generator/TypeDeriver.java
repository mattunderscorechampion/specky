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

import com.mattunderscore.specky.dsl.model.DSLImplementationDesc;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Fully derive a type from its superinterfaces.
 *
 * @author Matt Champion on 21/07/16
 */
public final class TypeDeriver {
    private final TypeResolver typeResolver;
    private final DefaultValueResolver valueResolver;
    private final Map<String, TypeDesc> views;

    /**
     * Constructor.
     */
    public TypeDeriver(TypeResolver typeResolver, DefaultValueResolver valueResolver, Map<String, TypeDesc> views) {
        this.typeResolver = typeResolver;
        this.valueResolver = valueResolver;
        this.views = views;
    }

    /**
     * @return the fully derived type
     */
    public ImplementationDesc deriveType(DSLSpecDesc specDesc, DSLImplementationDesc dslImplementationDesc) {
        if (dslImplementationDesc instanceof DSLValueDesc) {
            return ValueDesc
                .builder()
                .licence(specDesc.getLicence())
                .author(specDesc.getAuthor())
                .packageName(specDesc.getPackageName())
                .name(dslImplementationDesc.getName())
                .constructionMethod(dslImplementationDesc.getConstructionMethod())
                .properties(deriveProperties(dslImplementationDesc))
                .supertypes(dslImplementationDesc.getSupertypes())
                .description(dslImplementationDesc.getDescription())
                .build();
        }
        else {
            return BeanDesc
                .builder()
                .author(specDesc.getAuthor())
                .packageName(specDesc.getPackageName())
                .name(dslImplementationDesc.getName())
                .constructionMethod(dslImplementationDesc.getConstructionMethod())
                .properties(deriveProperties(dslImplementationDesc))
                .supertypes(dslImplementationDesc.getSupertypes())
                .description(dslImplementationDesc.getDescription())
                .build();
        }
    }

    private List<PropertyDesc> deriveProperties(DSLImplementationDesc dslImplementationDesc) {
        final List<TypeDesc> resolveSupertypes = resolveSupertypes(dslImplementationDesc);

        final List<PropertyDesc> inheritedProperties = resolveSupertypes
            .stream()
            .map(TypeDesc::getProperties)
            .flatMap(Collection::stream)
            .collect(toList());

        final List<PropertyDesc> declaredProperties = dslImplementationDesc
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
                checkMergableProperties(currentProperty, property);
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

    private List<TypeDesc> resolveSupertypes(DSLImplementationDesc dslImplementationDesc) {
        final List<TypeDesc> typeDescs = new ArrayList<>();
        resolveSupertypes(dslImplementationDesc, typeDescs, new HashSet<>());
        return typeDescs;
    }

    private void resolveSupertypes(
            DSLImplementationDesc dslImplementationDesc,
            List<TypeDesc> typeDescs,
            Set<TypeDesc> setOfTypes) {
        dslImplementationDesc
            .getSupertypes()
            .stream()
            .map(typeResolver::resolveOrThrow)
            .map(views::get).forEach(typeDesc -> {
                if (setOfTypes.add(typeDesc)) {
                    resolveSupertypes(typeDesc, typeDescs, setOfTypes);
                    typeDescs.add(typeDesc);
                }
            });
    }

    private void resolveSupertypes(TypeDesc firstTypeDesc, List<TypeDesc> typeDescs, Set<TypeDesc> setOfTypes) {
        firstTypeDesc
            .getSupertypes()
            .stream()
            .map(typeResolver::resolveOrThrow)
            .map(views::get).forEach(typeDesc -> {
                if (setOfTypes.add(typeDesc)) {
                    resolveSupertypes(typeDesc, typeDescs, setOfTypes);
                    typeDescs.add(typeDesc);
                }
            });
    }

    private void checkMergableProperties(PropertyDesc currentProperty, PropertyDesc newProperty) {
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
        checkMergableProperties(currentProperty, declaredProperty);
        return declaredProperty;
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
