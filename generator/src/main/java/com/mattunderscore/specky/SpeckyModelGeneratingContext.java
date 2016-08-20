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
import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.generator.ModelGenerator;
import com.mattunderscore.specky.type.resolver.PropertyTypeResolver;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.mattunderscore.specky.value.resolver.CompositeValueResolver;
import com.mattunderscore.specky.value.resolver.JavaStandardDefaultValueResolver;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;
import com.mattunderscore.specky.value.resolver.NullValueResolver;
import com.mattunderscore.specky.value.resolver.OptionalValueResolver;

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
        final MutableValueResolver mutableValueResolver = new MutableValueResolver();
        specs.forEach(spec -> {
            spec.getImportTypes().forEach(importDesc -> {
                final int lastPart = importDesc.getTypeName().lastIndexOf('.');
                final String packageName = importDesc.getTypeName().substring(0, lastPart);
                final String typeName = importDesc.getTypeName().substring(lastPart + 1);
                typeResolver.registerTypeName(packageName, typeName);
                if (importDesc.getDefaultValue() != null) {
                    mutableValueResolver.register(importDesc.getTypeName(), importDesc.getDefaultValue());
                }
            });
            spec.getViews().forEach(view -> typeResolver.registerTypeName(spec.getPackageName(), view.getName()));
            spec.getTypes().forEach(type -> typeResolver.registerTypeName(spec.getPackageName(), type.getName()));
        });

        final LicenceResolver licenceResolver = new LicenceResolver();
        specs.stream().map(DSLSpecDesc::getLicences).flatMap(List::stream).forEach(dslLicence -> {
            if (dslLicence.getIdentifier() == null) {
                licenceResolver.register(dslLicence.getLicence());
            }
            else {
                licenceResolver.register(dslLicence.getIdentifier(), dslLicence.getLicence());
            }
        });

        final TypeResolver resolver = new TypeResolverBuilder().registerResolver(typeResolver).build();
        modelGenerator = new ModelGenerator(
            specs,
            resolver,
            new PropertyTypeResolver(resolver),
            new CompositeValueResolver()
                .with(new OptionalValueResolver())
                .with(new JavaStandardDefaultValueResolver())
                .with(mutableValueResolver)
                .with(new NullValueResolver()),
            licenceResolver);
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
