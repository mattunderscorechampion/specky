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
import static javax.lang.model.element.Modifier.PRIVATE;

import java.util.List;

import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.FieldSpec.Builder;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for beans.
 * @author Matt Champion on 11/06/2016
 */
public final class BeanGenerator {
    private final TypeInitialiser typeInitialiser;
    private final TypeAppender constructionMethodAppender;
    private final TypeAppender superTypeAppender;
    private final List<MethodGeneratorForType> forTypeGenerators;
    private final List<MethodGeneratorForProperty> forPropertyGenerators;

    /**
     * Constructor.
     */
    public BeanGenerator(
            TypeInitialiser typeInitialiser,
            TypeAppender constructionMethodAppender,
            TypeAppender superTypeAppender,
            List<MethodGeneratorForProperty> methodGeneratorForProperties,
            List<MethodGeneratorForType> methodGeneratorForTypes) {

        this.typeInitialiser = typeInitialiser;
        this.constructionMethodAppender = constructionMethodAppender;
        this.superTypeAppender = superTypeAppender;
        this.forTypeGenerators = methodGeneratorForTypes;
        this.forPropertyGenerators = methodGeneratorForProperties;
    }

    /**
     * @return the bean type
     */
    public TypeSpec generateBean(SpecDesc specDesc, BeanDesc beanDesc) {
        final TypeSpec.Builder builder = typeInitialiser.create(specDesc, beanDesc);

        superTypeAppender.append(builder, specDesc, beanDesc);

        beanDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final TypeName type = getType(propertyDesc);
                final Builder fieldSpecBuilder = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE);

                if (beanDesc.getConstructionMethod() == ConstructionMethod.CONSTRUCTOR
                    && propertyDesc.getDefaultValue() != null) {
                    fieldSpecBuilder.initializer(propertyDesc.getDefaultValue());
                }

                builder.addField(fieldSpecBuilder.build());

                forPropertyGenerators
                    .forEach(generator -> builder.addMethod(generator.generate(specDesc, beanDesc, propertyDesc)));
            });

        constructionMethodAppender.append(builder, specDesc, beanDesc);

        forTypeGenerators
            .forEach(generator -> builder.addMethod(generator.generate(specDesc, beanDesc)));

        return builder.build();
    }

}
