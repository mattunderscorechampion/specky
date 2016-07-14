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

package com.mattunderscore.specky;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.model.generator.ModelGenerator;
import com.mattunderscore.specky.processed.model.SpecDesc;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.mattunderscore.specky.value.resolver.CompositeValueResolver;
import com.mattunderscore.specky.value.resolver.JavaStandardDefaultValueResolver;
import com.mattunderscore.specky.value.resolver.NullValueResolver;

/**
 * Generates a model from the DSL model.
 *
 * @author Matt Champion on 13/07/2016
 */
public final class SpeckyModelGeneratingContext {
    private final AtomicBoolean consumed = new AtomicBoolean(false);
    private final ModelGenerator modelGenerator;

    /*package*/ SpeckyModelGeneratingContext(List<DSLSpecDesc> specs) {
        final SpecTypeResolver typeResolver = new SpecTypeResolver();
        specs.forEach(spec -> {
            spec.getViews().forEach(view -> typeResolver.registerTypeName(spec.getPackageName(), view.getName()));
            spec.getValues().forEach(value -> typeResolver.registerTypeName(spec.getPackageName(), value.getName()));
        });

        modelGenerator = new ModelGenerator(
            specs,
            new TypeResolverBuilder().registerResolver(typeResolver).build(),
            new CompositeValueResolver()
                .with(new JavaStandardDefaultValueResolver())
                .with(new NullValueResolver()));
    }

    /**
     * Generate the Java code.
     * @throws IllegalStateException if has been called before
     */
    public SpeckyGeneratingContext generate() {
        if (consumed.compareAndSet(false, true)) {
            final SpecDesc specDesc = modelGenerator.get();
            return new SpeckyGeneratingContext(specDesc);
        }
        else {
            throw new IllegalStateException("Context has already been generated");
        }
    }
}
