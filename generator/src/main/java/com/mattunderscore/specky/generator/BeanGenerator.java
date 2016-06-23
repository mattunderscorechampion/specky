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

import static com.mattunderscore.specky.generator.GeneratorUtils.CONSTRUCTOR_DOC;
import static com.mattunderscore.specky.generator.GeneratorUtils.TYPE_DOC;
import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.FieldSpec.Builder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * @author Matt Champion on 11/06/2016
 */
public final class BeanGenerator {
    private final MutableBuilderGenerator mutableBuilderGenerator;
    private final ImmutableBuilderGenerator immutableBuilderGenerator;
    private final AccessorGenerator accessorGenerator;
    private final MutatorGenerator mutatorGenerator;

    public BeanGenerator(
            MutableBuilderGenerator mutableBuilderGenerator,
            ImmutableBuilderGenerator immutableBuilderGenerator,
            AccessorGenerator accessorGenerator,
            MutatorGenerator mutatorGenerator) {

        this.mutableBuilderGenerator = mutableBuilderGenerator;
        this.immutableBuilderGenerator = immutableBuilderGenerator;
        this.accessorGenerator = accessorGenerator;
        this.mutatorGenerator = mutatorGenerator;
    }

    public TypeSpec generateBean(SpecDesc specDesc, BeanDesc beanDesc) {
        final TypeSpec.Builder builder = TypeSpec
            .classBuilder(beanDesc.getName())
            .addModifiers(PUBLIC, FINAL)
            .addJavadoc(TYPE_DOC, "Bean", beanDesc.getName());

        beanDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final TypeName type = getType(propertyDesc.getType());
                final Builder fieldSpecBuilder = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE);

                if (beanDesc.getConstructionMethod() == ConstructionMethod.CONSTRUCTOR && propertyDesc.getDefaultValue() != null) {
                    fieldSpecBuilder.initializer(propertyDesc.getDefaultValue());
                }

                final FieldSpec fieldSpec = fieldSpecBuilder.build();
                final MethodSpec methodSpec = accessorGenerator.generateAccessor(fieldSpec, propertyDesc);

                builder
                    .addField(fieldSpec)
                    .addMethod(methodSpec)
                    .addMethod(mutatorGenerator.generateMutator(fieldSpec, propertyDesc));
            });

        if (beanDesc.getConstructionMethod() == ConstructionMethod.CONSTRUCTOR) {
            final MethodSpec constructor = constructorBuilder()
                .addModifiers(PUBLIC)
                .addJavadoc(CONSTRUCTOR_DOC)
                .build();
            builder.addMethod(constructor);
        }
        else if (beanDesc.getConstructionMethod() == ConstructionMethod.MUTABLE_BUILDER) {
            mutableBuilderGenerator.build(builder, specDesc, beanDesc);
        }
        else if (beanDesc.getConstructionMethod() == ConstructionMethod.IMMUTABLE_BUILDER) {
            immutableBuilderGenerator.build(builder, specDesc, beanDesc);
        }
        else {
            throw new IllegalArgumentException("Unsupported construction type");
        }

        return builder.build();
    }

}
