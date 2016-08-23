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

import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.generator.scope.ScopeResolver;

/**
 * Generator for the model from the DSL model.
 * @author Matt Champion on 12/07/2016
 */
public final class ModelGenerator implements Supplier<SpecDesc> {
    private final List<DSLSpecDesc> dslSpecs;
    private final ScopeResolver scopeResolver;
    private final TypeDeriver typeDeriver;

    /**
     * Constructor.
     */
    public ModelGenerator(
            List<DSLSpecDesc> dslSpecs,
            ScopeResolver scopeResolver,
            TypeDeriver typeDeriver) {
        this.dslSpecs = dslSpecs;
        this.scopeResolver = scopeResolver;
        this.typeDeriver = typeDeriver;
    }

    @Override
    public SpecDesc get() {

        final List<AbstractTypeDesc> types = dslSpecs
            .stream()
            .flatMap(this::getTypes)
            .collect(toList());

        final Map<String, AbstractTypeDesc> mappedTypes = types
            .stream()
            .collect(toMap(viewDesc -> viewDesc.getPackageName() + "." + viewDesc.getName(), viewDesc -> viewDesc));
        final ImplementationDeriver implementationDeriver = new ImplementationDeriver(scopeResolver, mappedTypes);

        return SpecDesc
            .builder()
            .implementations(
                dslSpecs
                    .stream()
                    .flatMap(dslSpecDesc -> getImplementations(implementationDeriver, dslSpecDesc))
                    .collect(toList()))
            .types(types)
            .build();
    }

    private Stream<ImplementationDesc> getImplementations(
            ImplementationDeriver implementationDeriver,
            DSLSpecDesc dslSpecDesc) {
        return dslSpecDesc
            .getImplementations()
            .stream()
            .map(dslTypeDesc -> implementationDeriver.deriveType(dslSpecDesc, dslTypeDesc));
    }

    private Stream<AbstractTypeDesc> getTypes(DSLSpecDesc dslSpecDesc) {
        return dslSpecDesc
            .getTypes()
            .stream()
            .map(dslTypeDesc -> typeDeriver.deriveType(dslSpecDesc, dslTypeDesc));
    }
}
