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

import com.mattunderscore.specky.constraint.model.BinaryConstraintOperator;
import com.mattunderscore.specky.constraint.model.ConstraintOperator;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ViewDesc;
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

    /**
     * Constructor.
     */
    public ModelGenerator(List<DSLSpecDesc> dslSpecs, TypeResolver typeResolver, DefaultValueResolver valueResolver) {
        this.dslSpecs = dslSpecs;
        this.typeResolver = typeResolver;
        this.valueResolver = valueResolver;
    }

    @Override
    public SpecDesc get() {

        final List<ViewDesc> views = dslSpecs
            .stream()
            .map(this::getViews)
            .flatMap(Collection::stream)
            .collect(toList());

        final Map<String, ViewDesc> mappedViews = views
            .stream()
            .collect(toMap(viewDesc -> viewDesc.getPackageName() + "." + viewDesc.getName(), viewDesc -> viewDesc));
        final TypeDeriver typeDeriver = new TypeDeriver(typeResolver, valueResolver, mappedViews);

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

    private List<TypeDesc> getTypes(TypeDeriver typeDeriver, DSLSpecDesc dslSpecDesc) {
        return dslSpecDesc
            .getTypes()
            .stream()
            .map(dslTypeDesc -> typeDeriver.deriveType(dslSpecDesc, dslTypeDesc))
            .collect(toList());
    }

    private List<ViewDesc> getViews(DSLSpecDesc dslSpecDesc) {
        return dslSpecDesc
            .getViews()
            .stream()
            .map(dslTypeDesc -> getView(dslSpecDesc.getAuthor(), dslSpecDesc.getPackageName(), dslTypeDesc))
            .collect(toList());
    }

    private ViewDesc getView(String author, String packageName, DSLViewDesc dslViewDesc) {
        final List<PropertyDesc> properties = dslViewDesc
            .getProperties()
            .stream()
            .map(this::getViewProperty)
            .collect(toList());

        return ViewDesc
            .builder()
            .author(author)
            .packageName(packageName)
            .name(dslViewDesc.getName())
            .properties(properties)
            .description(dslViewDesc.getDescription())
            .build();
    }

    private PropertyDesc getViewProperty(DSLPropertyDesc dslPropertyDesc) {
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
            .defaultValue(getDefaultValue(dslPropertyDesc, resolvedType))
            .constraint(dslPropertyDesc.getConstraint())
            .optional(dslPropertyDesc.isOptional())
            .override(true)
            .description(dslPropertyDesc.getDescription())
            .build();
    }

    private BinaryConstraintOperator toConstraintOperator(BinaryConstraintOperator operator) {
        switch (operator) {
            case DISJUNCTION:
                return BinaryConstraintOperator.DISJUNCTION;
            case CONJUNCTION:
                return BinaryConstraintOperator.CONJUNCTION;
            default:
                throw new IllegalArgumentException("Unsupported operation");
        }
    }

    private ConstraintOperator toConstraintOperator(ConstraintOperator operator) {
        switch (operator) {
            case LESS_THAN:
                return ConstraintOperator.LESS_THAN;
            case GREATER_THAN:
                return ConstraintOperator.GREATER_THAN;
            case LESS_THAN_OR_EQUAL:
                return ConstraintOperator.LESS_THAN_OR_EQUAL;
            case GREATER_THAN_OR_EQUAL:
                return ConstraintOperator.GREATER_THAN_OR_EQUAL;
            default:
                throw new IllegalArgumentException("Unsupported operation");
        }
    }

    private String getDefaultValue(DSLPropertyDesc dslPropertyDesc, String resolvedType) {
        final String defaultValue = dslPropertyDesc.getDefaultValue();

        if (defaultValue != null) {
            return defaultValue;
        }

        final String typeDefaultValue = valueResolver.resolve(resolvedType).get();
        if (!dslPropertyDesc.isOptional() && "null".equals(typeDefaultValue)) {
            throw new IllegalStateException(
                "The property " + dslPropertyDesc.getName() + " is not optional but has no default type");
        }

        return typeDefaultValue;
    }
}
