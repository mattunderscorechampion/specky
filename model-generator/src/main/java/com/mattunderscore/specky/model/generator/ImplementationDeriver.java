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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.mattunderscore.specky.SemanticErrorListener;
import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.constraint.model.NFDisjointPredicates;
import com.mattunderscore.specky.dsl.model.DSLImplementationDesc;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.model.generator.scope.Scope;
import com.mattunderscore.specky.model.generator.scope.ScopeResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Fully derive an implementation from its superinterfaces.
 *
 * @author Matt Champion on 21/07/16
 */
public final class ImplementationDeriver {
    private final ScopeResolver scopeResolver;
    private final Map<String, AbstractTypeDesc> abstractTypes;
    private final SemanticErrorListener semanticErrorListener;

    /**
     * Constructor.
     */
    public ImplementationDeriver(
        ScopeResolver scopeResolver,
        Map<String, AbstractTypeDesc> abstractTypes,
        SemanticErrorListener semanticErrorListener) {
        this.scopeResolver = scopeResolver;
        this.abstractTypes = abstractTypes;
        this.semanticErrorListener = semanticErrorListener;
    }

    /**
     * @return the fully derived type
     */
    public ImplementationDesc deriveType(DSLSpecDesc specDesc, DSLImplementationDesc dslImplementationDesc) {
        final Scope scope = scopeResolver.resolve(specDesc);

        if (dslImplementationDesc instanceof DSLValueDesc) {
            return ValueDesc
                .builder()
                .licence(scope.getLicenceResolver().resolve(dslImplementationDesc.getLicence()).orElse(null))
                .author(specDesc.getAuthor())
                .packageName(specDesc.getPackageName())
                .name(dslImplementationDesc.getName())
                .constructionMethod(dslImplementationDesc.getConstructionMethod())
                .withModification(dslImplementationDesc.isWithModification())
                .properties(deriveProperties(scope, dslImplementationDesc))
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
                .withModification(dslImplementationDesc.isWithModification())
                .properties(deriveProperties(scope, dslImplementationDesc))
                .supertypes(dslImplementationDesc.getSupertypes())
                .description(dslImplementationDesc.getDescription())
                .build();
        }
    }

    private List<PropertyDesc> deriveProperties(Scope scope, DSLImplementationDesc dslImplementationDesc) {
        final List<AbstractTypeDesc> resolveSupertypes = resolveSupertypes(scope, dslImplementationDesc);

        final List<PropertyDesc> inheritedProperties = resolveSupertypes
            .stream()
            .map(AbstractTypeDesc::getProperties)
            .flatMap(Collection::stream)
            .collect(toList());

        final List<PropertyDesc> declaredProperties = dslImplementationDesc
            .getProperties()
            .stream()
            .map(property -> get(scope, property))
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

    private List<AbstractTypeDesc> resolveSupertypes(Scope scope, DSLImplementationDesc dslImplementationDesc) {
        final List<AbstractTypeDesc> typeDescs = new ArrayList<>();
        resolveSupertypes(scope, dslImplementationDesc, typeDescs, new HashSet<>());
        return typeDescs;
    }

    private void resolveSupertypes(
            Scope scope,
            DSLImplementationDesc dslImplementationDesc,
            List<AbstractTypeDesc> typeDescs,
            Set<AbstractTypeDesc> setOfTypes) {
        dslImplementationDesc
            .getSupertypes()
            .stream()
            .map(typeName -> {
                final Optional<String> optionalType = scope.getTypeResolver().resolve(typeName);
                if (!optionalType.isPresent()) {
                    semanticErrorListener.onSemanticError("No resolvable type for " + typeName);
                }
                return optionalType.orElse("unknown type");
            })
            .map(abstractTypes::get).forEach(typeDesc -> {
                if (setOfTypes.add(typeDesc)) {
                    resolveSupertypes(scope, typeDesc, typeDescs, setOfTypes);
                    typeDescs.add(typeDesc);
                }
            });
    }

    private void resolveSupertypes(
            Scope scope,
            AbstractTypeDesc firstTypeDesc,
            List<AbstractTypeDesc> typeDescs,
            Set<AbstractTypeDesc> setOfTypes) {
        firstTypeDesc
            .getSupertypes()
            .stream()
            .map(typeName -> {
                final Optional<String> optionalType = scope.getTypeResolver().resolve(typeName);
                if (!optionalType.isPresent()) {
                    semanticErrorListener.onSemanticError("No resolvable type for " + typeName);
                }
                return optionalType.orElse("unknown type");
            })
            .map(abstractTypes::get).forEach(typeDesc -> {
                if (setOfTypes.add(typeDesc)) {
                    resolveSupertypes(scope, typeDesc, typeDescs, setOfTypes);
                    typeDescs.add(typeDesc);
                }
            });
    }

    private void checkMergableProperties(PropertyDesc currentProperty, PropertyDesc newProperty) {
        if (!newProperty.getType().equals(currentProperty.getType())) {
            semanticErrorListener.onSemanticError("Conflicting property declarations for " +
                currentProperty.getName() +
                ". Types " +
                currentProperty.getType() +
                " and " +
                newProperty.getType());
        }
        else if (newProperty.isOptional() != currentProperty.isOptional()) {
            semanticErrorListener.onSemanticError("Conflicting property declarations for " +
                currentProperty.getName() +
                ". Cannot be both optional and required.");
        }
    }

    private PropertyDesc mergeDeclaredProperty(PropertyDesc currentProperty, PropertyDesc declaredProperty) {
        checkMergableProperties(currentProperty, declaredProperty);

        final List<NFDisjointPredicates> constraints = new ArrayList<>();

        if (currentProperty.getConstraint() != null) {
            constraints.addAll(currentProperty.getConstraint().getPredicates());
        }
        if (declaredProperty.getConstraint() != null) {
            constraints.addAll(declaredProperty.getConstraint().getPredicates());
        }

        return PropertyDesc
            .builder()
            .name(declaredProperty.getName())
            .description(declaredProperty.getDescription())
            .type(declaredProperty.getType())
            .typeParameters(declaredProperty.getTypeParameters())
            .optional(declaredProperty.isOptional())
            .defaultValue(declaredProperty.getDefaultValue())
            .override(declaredProperty.isOverride())
            .constraint(NFConjoinedDisjointPredicates
                .builder()
                .predicates(constraints)
                .build())
            .build();
    }

    private PropertyDesc get(Scope scope, DSLPropertyDesc dslPropertyDesc) {
        final String defaultValueExpression = dslPropertyDesc.getDefaultValue();
        final CodeBlock defaultValue = defaultValueExpression == null ?
                CodeBlock.of("null") :
                CodeBlock.of(defaultValueExpression);
        final Optional<String> optionalPropertyType = scope.getPropertyTypeResolver().resolve(dslPropertyDesc);
        if (!optionalPropertyType.isPresent()) {
            semanticErrorListener.onSemanticError("No resolvable type for " + dslPropertyDesc.getName());
        }
        final String resolvedType = optionalPropertyType.orElse("unknown type");

        return PropertyDesc
            .builder()
            .name(dslPropertyDesc.getName())
            .type(resolvedType)
            .typeParameters(dslPropertyDesc
                .getTypeParameters()
                .stream()
                .map(typeName -> {
                    final Optional<String> optionalType = scope.getTypeResolver().resolve(typeName);
                    if (!optionalType.isPresent()) {
                        semanticErrorListener.onSemanticError("No resolvable type for " + typeName);
                    }
                    return optionalType.orElse("unknown type");
                })
                .collect(toList()))
            .defaultValue(
                defaultValue.equals(CodeBlock.of("null")) && !dslPropertyDesc.isOptional() ?
                    scope.getValueResolver().resolve(dslPropertyDesc, resolvedType).get() :
                    defaultValue)
            .constraint(dslPropertyDesc.getConstraint())
            .optional(dslPropertyDesc.isOptional())
            .override(false)
            .description(dslPropertyDesc.getDescription())
            .build();
    }
}
