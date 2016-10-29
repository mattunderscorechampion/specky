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

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;

import java.util.List;

import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for value types.
 * @author Matt Champion on 11/06/2016
 */
public final class ValueGenerator {
    private final TypeInitialiser typeInitialiser;
    private final TypeAppender constructionMethodAppender;
    private final TypeAppender superTypeAppender;
    private final List<MethodGeneratorForType> forTypeGenerators;
    private final List<MethodGeneratorForProperty> forPropertyGenerators;

    /**
     * Constructor.
     */
    public ValueGenerator(
            TypeInitialiser typeInitialiser,
            TypeAppender constructionMethodAppender,
            TypeAppender superTypeAppender,
            List<MethodGeneratorForProperty> methodGeneratorForProperties,
            List<MethodGeneratorForType> methodGeneratorForTypes) {

        this.typeInitialiser = typeInitialiser;
        this.constructionMethodAppender = constructionMethodAppender;
        this.superTypeAppender = superTypeAppender;
        this.forPropertyGenerators = methodGeneratorForProperties;
        this.forTypeGenerators = methodGeneratorForTypes;
    }

    /**
     * @return the value type
     */
    public TypeSpec generateValue(SpecDesc specDesc, ValueDesc valueDesc) {
        final TypeSpec.Builder builder = typeInitialiser.create(specDesc, valueDesc);

        superTypeAppender.append(builder, specDesc, valueDesc);

        constructionMethodAppender.append(builder, specDesc, valueDesc);

        valueDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final TypeName type = getType(propertyDesc);
                builder.addField(FieldSpec.builder(type, propertyDesc.getName(), PRIVATE, FINAL).build());
                forPropertyGenerators
                    .stream().map(generator -> generator.generate(specDesc, valueDesc, propertyDesc))
                    .filter(methodSpec -> methodSpec != null)
                    .forEach(builder::addMethod);
            });

        forTypeGenerators.forEach(generator -> builder.addMethod(generator.generate(specDesc, valueDesc)));

        return builder.build();
    }
}
