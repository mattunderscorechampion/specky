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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mattunderscore.specky.SemanticErrorListener;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.generator.scope.ScopeResolver;

/**
 * Generator for the model from the DSL model.
 * @author Matt Champion on 12/07/2016
 */
public final class ModelGenerator implements Supplier<SpecDesc> {
    private final List<DSLSpecDesc> dslSpecs;
    private final ScopeResolver scopeResolver;
    private final TypeDeriver typeDeriver;
    private final SemanticErrorListener semanticErrorListener;

    /**
     * Constructor.
     */
    public ModelGenerator(
        List<DSLSpecDesc> dslSpecs,
        ScopeResolver scopeResolver,
        TypeDeriver typeDeriver,
        SemanticErrorListener semanticErrorListener) {
        this.dslSpecs = dslSpecs;
        this.scopeResolver = scopeResolver;
        this.typeDeriver = typeDeriver;
        this.semanticErrorListener = semanticErrorListener;
    }

    @Override
    public SpecDesc get() {

        final List<AbstractTypeDesc> abstractTypes = dslSpecs
            .stream()
            .flatMap(dslSpec -> dslSpec.getTypes().stream().map(dslType -> typeDeriver.deriveType(dslSpec, dslType)))
            .collect(toList());

        final Map<String, AbstractTypeDesc> mappedAbstractTypes = abstractTypes
            .stream()
            .collect(toMap(viewDesc -> viewDesc.getPackageName() + "." + viewDesc.getName(), viewDesc -> viewDesc));

        final ImplementationDeriver implementationDeriver = new ImplementationDeriver(
            scopeResolver,
            mappedAbstractTypes,
            semanticErrorListener);

        final List<ImplementationDesc> implementations = dslSpecs
            .stream()
            .flatMap(dslSpec -> dslSpec
                .getImplementations()
                .stream()
                .map(dslImplementation -> implementationDeriver.deriveType(dslSpec, dslImplementation)))
            .collect(toList());

        final List<TypeDesc> types = Stream
            .concat(
                abstractTypes.stream(),
                implementations.stream())
            .collect(toList());

        return SpecDesc
            .builder()
            .implementations(implementations)
            .abstractTypes(abstractTypes)
            .types(types)
            .build();
    }
}
