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

import static com.mattunderscore.specky.generator.GeneratorUtils.BUILDER_FACTORY;
import static com.mattunderscore.specky.generator.GeneratorUtils.BUILDER_TYPE_DOC;
import static com.mattunderscore.specky.generator.GeneratorUtils.CONSTRUCTOR_DOC;
import static com.mattunderscore.specky.generator.GeneratorUtils.MUTABLE_BUILDER_SETTER;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * @author Matt Champion on 13/06/2016
 */
public final class MutableBuilderGenerator {
    private final BuildMethodGenerator buildMethodGenerator;

    public MutableBuilderGenerator(BuildMethodGenerator buildMethodGenerator) {
        this.buildMethodGenerator = buildMethodGenerator;
    }

    public TypeSpec.Builder build(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, TypeDesc valueDesc) {
        final MethodSpec.Builder constructor = constructorBuilder()
            .addModifiers(PRIVATE)
            .addJavadoc(CONSTRUCTOR_DOC);

        final TypeSpec.Builder builder = classBuilder("Builder")
            .addModifiers(PUBLIC, FINAL, STATIC)
            .addJavadoc(BUILDER_TYPE_DOC, valueDesc.getName());

        valueDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final ClassName type = ClassName.bestGuess(propertyDesc.getType());
                final FieldSpec fieldSpec = FieldSpec
                    .builder(type, propertyDesc.getName(), PRIVATE, FINAL)
                    .initializer(propertyDesc.getDefaultValue() == null ? "null" : propertyDesc.getDefaultValue())
                    .build();
                final FieldSpec builderFieldSpec = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE).build();

                final ParameterSpec constructorParameter = ParameterSpec.builder(type, propertyDesc.getName()).build();

                constructor
                    .addParameter(constructorParameter)
                    .addStatement("this.$N = $N", fieldSpec, constructorParameter);

                final MethodSpec configuator = methodBuilder(propertyDesc.getName())
                    .addModifiers(PUBLIC)
                    .addJavadoc(MUTABLE_BUILDER_SETTER, propertyDesc.getName())
                    .returns(ClassName.get(specDesc.getPackageName(), valueDesc.getName(), "Builder"))
                    .addParameter(constructorParameter)
                    .addStatement("this.$N = $N", builderFieldSpec, constructorParameter)
                    .addStatement("return this")
                    .build();

                builder.addField(builderFieldSpec).addMethod(configuator);
            });

        builder.addMethod(constructorBuilder().addModifiers(PRIVATE).build());

        builder.addMethod(buildMethodGenerator.generateBuildMethod(specDesc, valueDesc));

        typeSpecBuilder
            .addMethod(methodBuilder("builder")
                .returns(ClassName.get(specDesc.getPackageName(), valueDesc.getName(), "Builder"))
                .addModifiers(PUBLIC, STATIC)
                .addJavadoc(BUILDER_FACTORY, valueDesc.getName())
                .addStatement("return new Builder()")
                .build());

        return typeSpecBuilder
            .addMethod(constructor.build())
            .addType(builder.build());
    }
}
