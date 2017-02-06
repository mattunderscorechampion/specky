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

package com.mattunderscore.specky.generator.builder.mutable;

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.mattunderscore.specky.javapoet.javadoc.JavaDocBuilder.docMethod;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.util.Arrays.asList;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import java.util.List;

import com.mattunderscore.specky.generator.TypeAppender;
import com.mattunderscore.specky.generator.TypeAppenderForProperty;
import com.mattunderscore.specky.generator.TypeInitialiser;
import com.mattunderscore.specky.generator.builder.BuildMethodGenerator;
import com.mattunderscore.specky.generator.builder.CollectionAddConfiguratorGenerator;
import com.mattunderscore.specky.generator.builder.SettingConfiguratorGenerator;
import com.mattunderscore.specky.generator.constructor.ConstructorForBuiltTypeGenerator;
import com.mattunderscore.specky.generator.statements.This;
import com.mattunderscore.specky.generator.statements.UpdateCollection;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for mutable builders.
 * @author Matt Champion on 13/06/2016
 */
public final class MutableBuilderGenerator implements TypeAppender<ImplementationDesc> {
    private static final List<String> COLLECTION_TYPES = asList("java.util.Set", "java.util.List");
    private final TypeInitialiser<ImplementationDesc> typeInitialiser;
    private final TypeAppender<ImplementationDesc> constructorGenerator =
        new ConstructorForBuiltTypeGenerator();
    private final TypeAppenderForProperty<ImplementationDesc> settingConfiguratorGenerator =
        new SettingConfiguratorGenerator(
            docMethod()
                .setMethodDescription("Method to configure property $L on the builder.")
                .setReturnsDescription("this builder")
                .toJavaDoc(),
            new This());
    private final TypeAppenderForProperty<ImplementationDesc> collectionAddConfiguratorGenerator =
        new CollectionAddConfiguratorGenerator(
            docMethod()
                .setMethodDescription("Method to add an element to property $L on the builder.")
                .setReturnsDescription("this builder")
                .toJavaDoc(),
                new UpdateCollection(),
                new This());
    private final TypeAppender<ImplementationDesc> supplierConditional =
        new SupplierConditionalConsumerConfiguratorGenerator(
            docMethod()
                .setMethodDescription("Passes the builder to a consumer to allow it the opportunity of changing it " +
                    "if and only if the condition is {@code true}.")
                .addParameter("condition", "the condition to evaluate")
                .addParameter("consumer", "the consumer")
                .setReturnsDescription("this builder")
                .toJavaDoc());
    private final TypeAppender<ImplementationDesc> booleanConditional =
        new BooleanConditionalConsumerConfiguratorGenerator(
            docMethod()
                .setMethodDescription("Passes the builder to a consumer to allow it the opportunity of changing it" +
                    " if and only if the condition is {@code true}.")
                .addParameter("condition", "the condition")
                .addParameter("consumer", "the consumer")
                .setReturnsDescription("this builder")
                .toJavaDoc());
    private final TypeAppender<ImplementationDesc> consumerConfiguratorGenerator =
        new ConsumerConfiguratorGenerator(
            docMethod()
                .setMethodDescription("Passes the builder to a consumer to allow it the opportunity of changing it.")
                .addParameter("consumer", "the consumer")
                .setReturnsDescription("this builder")
                .toJavaDoc());
    private final BuildMethodGenerator buildMethodGenerator;

    /**
     * Constructor.
     */
    public MutableBuilderGenerator(
            TypeInitialiser<ImplementationDesc> typeInitialiser,
            BuildMethodGenerator buildMethodGenerator) {
        this.typeInitialiser = typeInitialiser;
        this.buildMethodGenerator = buildMethodGenerator;
    }

    @Override
    public void append(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, ImplementationDesc valueDesc) {
        final TypeSpec.Builder builder = typeInitialiser.create(specDesc, valueDesc);

        valueDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final TypeName type = getType(propertyDesc);
                final FieldSpec fieldSpec = FieldSpec
                    .builder(type, propertyDesc.getName(), PRIVATE)
                    .initializer(propertyDesc.getDefaultValue() == null ?
                        CodeBlock.of("null") :
                        propertyDesc.getDefaultValue())
                    .build();

                builder.addField(fieldSpec);

                settingConfiguratorGenerator.append(builder, specDesc, valueDesc, propertyDesc);

                if (COLLECTION_TYPES.contains(propertyDesc.getType())) {
                    collectionAddConfiguratorGenerator.append(builder, specDesc, valueDesc, propertyDesc);
                }
            });

        builder.addMethod(constructorBuilder().addModifiers(PRIVATE).build());
        booleanConditional.append(builder, specDesc, valueDesc);
        supplierConditional.append(builder, specDesc, valueDesc);
        buildMethodGenerator.append(builder, specDesc, valueDesc);
        consumerConfiguratorGenerator.append(builder, specDesc, valueDesc);

        typeSpecBuilder
            .addMethod(methodBuilder("builder")
                .returns(ClassName.get(valueDesc.getPackageName(), valueDesc.getName(), "Builder"))
                .addModifiers(PUBLIC, STATIC)
                .addJavadoc(
                    docMethod()
                        .setMethodDescription("Factory method for builder.")
                        .setReturnsDescription("a new builder for $L")
                        .toJavaDoc(),
                    valueDesc.getName())
                .addStatement("return new Builder()")
                .build());
        constructorGenerator.append(typeSpecBuilder, specDesc, valueDesc);
        typeSpecBuilder.addType(builder.build());
    }
}
