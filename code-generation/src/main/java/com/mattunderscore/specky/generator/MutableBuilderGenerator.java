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

import static com.mattunderscore.specky.generator.GeneratorUtils.BOOLEAN_CONDITIONAL_MUTABLE_BUILDER_SETTER;
import static com.mattunderscore.specky.generator.GeneratorUtils.BUILDER_FACTORY;
import static com.mattunderscore.specky.generator.GeneratorUtils.SUPPLIER_CONDITIONAL_MUTABLE_BUILDER_SETTER;
import static com.mattunderscore.specky.generator.GeneratorUtils.MUTABLE_BUILDER_SETTER;
import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for mutable builders.
 * @author Matt Champion on 13/06/2016
 */
public final class MutableBuilderGenerator implements TypeAppender {
    private final TypeInitialiser typeInitialiser;
    private final MethodGeneratorForType constructorGenerator = new ConstructorForBuiltTypeGenerator();
    private final MethodGeneratorForProperty settingConfiguratorGenerator =
        new SettingConfiguratorGenerator(MUTABLE_BUILDER_SETTER, new This());
    private final MethodGeneratorForType supplierConditional = new SupplierConditionalConfiguratorGenerator(
        SUPPLIER_CONDITIONAL_MUTABLE_BUILDER_SETTER);
    private final MethodGeneratorForType booleanConditional = new BooleanConditionalConfiguratorGenerator(
        BOOLEAN_CONDITIONAL_MUTABLE_BUILDER_SETTER);
    private final BuildMethodGenerator buildMethodGenerator;

    /**
     * Constructor.
     */
    public MutableBuilderGenerator(TypeInitialiser typeInitialiser, BuildMethodGenerator buildMethodGenerator) {
        this.typeInitialiser = typeInitialiser;
        this.buildMethodGenerator = buildMethodGenerator;
    }

    @Override
    public void append(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, TypeDesc valueDesc) {
        final TypeSpec.Builder builder = typeInitialiser.create(specDesc, valueDesc);

        valueDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final TypeName type = getType(propertyDesc);
                final FieldSpec fieldSpec = FieldSpec
                    .builder(type, propertyDesc.getName(), PRIVATE)
                    .initializer(propertyDesc.getDefaultValue() == null ? "null" : propertyDesc.getDefaultValue())
                    .build();

                builder
                    .addField(fieldSpec)
                    .addMethod(settingConfiguratorGenerator.generate(specDesc, valueDesc, propertyDesc));
            });

        builder
            .addMethod(constructorBuilder().addModifiers(PRIVATE).build())
            .addMethod(booleanConditional.generate(specDesc, valueDesc))
            .addMethod(supplierConditional.generate(specDesc, valueDesc))
            .addMethod(buildMethodGenerator.generate(specDesc, valueDesc));

        typeSpecBuilder
            .addMethod(methodBuilder("builder")
                .returns(ClassName.get(valueDesc.getPackageName(), valueDesc.getName(), "Builder"))
                .addModifiers(PUBLIC, STATIC)
                .addJavadoc(BUILDER_FACTORY, valueDesc.getName())
                .addStatement("return new Builder()")
                .build())
            .addMethod(constructorGenerator.generate(specDesc, valueDesc))
            .addType(builder.build());
    }
}
