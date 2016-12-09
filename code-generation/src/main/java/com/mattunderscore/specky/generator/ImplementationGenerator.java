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

package com.mattunderscore.specky.generator;

import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Objects;

/**
 * Generator for implementations.
 *
 * @author Matt Champion on 07/12/2016
 */
public final class ImplementationGenerator {
    private final TypeInitialiser typeInitialiser;
    private final TypeAppender<? super ImplementationDesc> constructionMethodAppender;
    private final TypeAppender<? super ImplementationDesc> superTypeAppender;
    private final FieldGeneratorForProperty fieldGeneratorForProperty;
    private final List<MethodGeneratorForType<? super ImplementationDesc>> forTypeGenerators;
    private final List<MethodGeneratorForProperty<? super ImplementationDesc>> forPropertyGenerators;

    /**
     * Constructor.
     */
    public ImplementationGenerator(
            TypeInitialiser typeInitialiser,
            TypeAppender<? super ImplementationDesc> constructionMethodAppender,
            TypeAppender<? super ImplementationDesc> superTypeAppender,
            FieldGeneratorForProperty fieldGeneratorForProperty,
            List<MethodGeneratorForProperty<? super ImplementationDesc>> methodGeneratorForProperties,
            List<MethodGeneratorForType<? super ImplementationDesc>> methodGeneratorForTypes) {

        this.typeInitialiser = typeInitialiser;
        this.constructionMethodAppender = constructionMethodAppender;
        this.superTypeAppender = superTypeAppender;
        this.fieldGeneratorForProperty = fieldGeneratorForProperty;
        this.forPropertyGenerators = methodGeneratorForProperties;
        this.forTypeGenerators = methodGeneratorForTypes;
    }

    /**
     * @return the type
     */
    public TypeSpec generate(SpecDesc specDesc, ImplementationDesc implementationDesc) {
        final TypeSpec.Builder builder = typeInitialiser.create(specDesc, implementationDesc);

        superTypeAppender.append(builder, specDesc, implementationDesc);

        constructionMethodAppender.append(builder, specDesc, implementationDesc);

        implementationDesc
            .getProperties()
            .forEach(propertyDesc -> {

                builder.addField(fieldGeneratorForProperty.generate(specDesc, implementationDesc, propertyDesc));

                forPropertyGenerators
                    .stream().map(generator -> generator.generate(specDesc, implementationDesc, propertyDesc))
                    .filter(Objects::nonNull)
                    .forEach(builder::addMethod);
            });

        forTypeGenerators.forEach(generator -> builder.addMethod(generator.generate(specDesc, implementationDesc)));

        return builder.build();
    }
}
