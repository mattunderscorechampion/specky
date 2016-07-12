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
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mattunderscore.specky.dsl.model.DSLConstructionMethod;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLPropertyImplementationDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.processed.model.ConstructionMethod;
import com.mattunderscore.specky.processed.model.PropertyImplementationDesc;
import com.mattunderscore.specky.processed.model.SpecDesc;
import com.mattunderscore.specky.processed.model.TypeDesc;
import com.mattunderscore.specky.processed.model.ValueDesc;

/**
 * Generator for the model from the DSL model.
 * @author Matt Champion on 12/07/2016
 */
public final class ModelGenerator implements Supplier<SpecDesc> {
    private final List<DSLSpecDesc> dslSpecs;

    public ModelGenerator(List<DSLSpecDesc> dslSpecs) {
        this.dslSpecs = dslSpecs;
    }

    @Override
    public SpecDesc get() {
        final Map<String, DSLViewDesc> views = dslSpecs
            .stream()
            .map(DSLSpecDesc::getViews)
            .flatMap(Collection::stream)
            .collect(toMap(DSLViewDesc::getName, view -> view));

        return SpecDesc
            .builder()
            .values(
                dslSpecs
                    .stream()
                    .map(dslSpecDesc -> get(views, dslSpecDesc))
                    .flatMap(Collection::stream)
                    .collect(toList()))
            .build();
    }

    private List<TypeDesc> get(Map<String, DSLViewDesc> views, DSLSpecDesc dslSpecDesc) {
        final String packageName = dslSpecDesc.getPackageName();
        return dslSpecDesc
            .getValues()
            .stream()
            .map(dslTypeDesc -> get(views, packageName, dslTypeDesc))
            .collect(Collectors.toList());
    }

    private TypeDesc get(Map<String, DSLViewDesc> views, String packageName, DSLTypeDesc dslTypeDesc) {
        final List<PropertyImplementationDesc> inheritedProperties = dslTypeDesc
            .getExtend()
            .stream()
            .map(views::get)
            .map(DSLViewDesc::getProperties)
            .flatMap(Collection::stream)
            .map(this::get)
            .collect(toList());

        final List<PropertyImplementationDesc> declaredProperties = dslTypeDesc
            .getProperties()
            .stream()
            .map(this::get)
            .collect(Collectors.toList());

        final List<PropertyImplementationDesc> allProperties = new ArrayList<>();
        allProperties.addAll(inheritedProperties);
        allProperties.addAll(declaredProperties);

        return ValueDesc
            .builder()
            .packageName(packageName)
            .name(dslTypeDesc.getName())
            .constructionMethod(get(dslTypeDesc.getConstructionMethod()))
            .properties(allProperties)
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

    private PropertyImplementationDesc get(DSLPropertyImplementationDesc dslTypeDesc) {
        return PropertyImplementationDesc
            .builder()
            .name(dslTypeDesc.getName())
            .type(dslTypeDesc.getType())
            .override(false)
            .build();
    }

    private PropertyImplementationDesc get(DSLPropertyDesc dslTypeDesc) {
        return PropertyImplementationDesc
            .builder()
            .name(dslTypeDesc.getName())
            .type(dslTypeDesc.getType())
            .override(true)
            .build();
    }
}
