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

package com.mattunderscore.specky.generator.builder.immutable;

import com.mattunderscore.specky.generator.LiteralValueGenerator;
import com.mattunderscore.specky.generator.TypeAppender;
import com.mattunderscore.specky.generator.TypeAppenderForProperty;
import com.mattunderscore.specky.generator.TypeInitialiser;
import com.mattunderscore.specky.generator.builder.BuildMethodGenerator;
import com.mattunderscore.specky.generator.builder.CollectionAddConfiguratorGenerator;
import com.mattunderscore.specky.generator.builder.InstantiateNewBuilder;
import com.mattunderscore.specky.generator.builder.SettingConfiguratorGenerator;
import com.mattunderscore.specky.generator.constructor.ConstructorForBuiltTypeGenerator;
import com.mattunderscore.specky.generator.statements.NewModifiedCollection;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.mattunderscore.specky.javapoet.javadoc.JavaDocBuilder.docMethod;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.util.Arrays.asList;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Generator for immutable builders.
 * @author Matt Champion on 15/06/2016
 */
public final class ImmutableBuilderGenerator implements TypeAppender<ImplementationDesc> {
    private static final List<String> COLLECTION_TYPES = asList("java.util.Set", "java.util.List");
    private final TypeInitialiser<ImplementationDesc> typeInitialiser;
    private final TypeAppender<ImplementationDesc> constructorGenerator =
        new ConstructorForBuiltTypeGenerator();
    private final TypeAppender<ImplementationDesc> conditionalGenerator =
        new SupplierConditionalConfiguratorGenerator(
            docMethod()
                .setMethodDescription(
                    "Applies the function to the builder if and only if the condition is {@code true}.")
                .addParameter("condition", "the condition to evaluate")
                .addParameter("function", "the function to apply")
                .setReturnsDescription("a new builder if the condition is {@code true}, otherwise this builder")
                .toJavaDoc());
    private final TypeAppenderForProperty<ImplementationDesc> settingConfiguratorGenerator =
        new SettingConfiguratorGenerator(
            docMethod()
                .setMethodDescription("Method to configure property $L on the builder.")
                .setReturnsDescription("a new builder")
                .toJavaDoc(),
            new InstantiateNewBuilder());
    private final TypeAppenderForProperty<ImplementationDesc> collectionAddConfiguratorGenerator =
        new CollectionAddConfiguratorGenerator(
            docMethod()
                .setMethodDescription("Method to add an element to property $L on the builder.")
                .setReturnsDescription("a new builder")
                .toJavaDoc(),
                new NewModifiedCollection(),
                new InstantiateNewBuilder());
    private final TypeAppender<ImplementationDesc> booleanConditional = new BooleanConditionalConfiguratorGenerator(
        docMethod()
            .setMethodDescription("Applies the function to the builder if and only if the condition is {@code true}.")
            .addParameter("condition", "the condition")
            .addParameter("function", "the function to apply")
            .setReturnsDescription("a new builder if the condition is {@code true}, otherwise this builder")
            .toJavaDoc());
    private final TypeAppender<ImplementationDesc> functionalConfiguratorGenerator =
        new FunctionalConfiguratorGenerator(
            docMethod()
                .setMethodDescription("Applies the function to the builder.")
                .addParameter("function", "the function to apply")
                .setReturnsDescription("a new builder")
                .toJavaDoc());
    private final BuildMethodGenerator buildMethodGenerator;
    private final LiteralValueGenerator literalValueGenerator;

    /**
     * Constructor.
     */
    public ImmutableBuilderGenerator(
            TypeInitialiser<ImplementationDesc> typeInitialiser,
            BuildMethodGenerator buildMethodGenerator) {
        this.typeInitialiser = typeInitialiser;
        this.buildMethodGenerator = buildMethodGenerator;
        literalValueGenerator = new LiteralValueGenerator();
    }

    @Override
    public void append(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, ImplementationDesc valueDesc) {
        final TypeSpec.Builder builder = typeInitialiser.create(specDesc, valueDesc);

        valueDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final TypeName type = getType(propertyDesc);
                final FieldSpec builderFieldSpec = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE).build();

                builder
                    .addField(builderFieldSpec);

                settingConfiguratorGenerator.append(builder, specDesc, valueDesc, propertyDesc);

                if (COLLECTION_TYPES.contains(propertyDesc.getType())) {
                    collectionAddConfiguratorGenerator.append(builder, specDesc, valueDesc, propertyDesc);
                }
            });

        constructorGenerator.append(builder, specDesc, valueDesc);
        booleanConditional.append(builder, specDesc, valueDesc);
        conditionalGenerator.append(builder, specDesc, valueDesc);
        functionalConfiguratorGenerator.append(builder, specDesc, valueDesc);
        buildMethodGenerator.append(builder, specDesc, valueDesc);

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
                .addCode(defaultBuilder(valueDesc))
                .build());
        constructorGenerator.append(typeSpecBuilder, specDesc, valueDesc);
        typeSpecBuilder.addType(builder.build());
    }

    private CodeBlock defaultBuilder(ImplementationDesc valueDesc) {
        final Builder builder = CodeBlock.builder().add("$[return new Builder(");
        final List<CodeBlock> codeBlocks = valueDesc
            .getProperties()
            .stream()
            .map(propertySpec ->
                propertySpec.getDefaultValue() == null ?
                    CodeBlock.of("null") :
                    literalValueGenerator.generate(propertySpec.getDefaultValue()))
            .collect(Collectors.toList());
        final Iterator<CodeBlock> iterator = codeBlocks.iterator();
        while (iterator.hasNext()) {
            builder.add(iterator.next());
            if (iterator.hasNext()) {
                builder.add(", ");
            }
        }

        return builder.add(");$]").build();
    }
}
