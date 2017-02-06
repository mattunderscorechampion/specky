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

package com.mattunderscore.specky.generator.builder;

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.lang.Character.toUpperCase;
import static java.util.Arrays.asList;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.List;
import java.util.Objects;

import com.mattunderscore.specky.generator.TypeAppenderForProperty;
import com.mattunderscore.specky.generator.statements.StatementAppenderForProperty;
import com.mattunderscore.specky.generator.statements.StatementGeneratorForType;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for add to collection configurator.
 *
 * @author Matt Champion on 16/09/16
 */
public final class CollectionAddConfiguratorGenerator implements TypeAppenderForProperty<ImplementationDesc> {
    private static final List<String> COLLECTION_TYPES = asList("java.util.Set", "java.util.List");
    private final String javadoc;
    private final StatementAppenderForProperty updateStatementGenerator;
    private final StatementGeneratorForType returnStatementGenerator;

    /**
     * Constructor.
     */
    public CollectionAddConfiguratorGenerator(
            String javadoc,
            StatementAppenderForProperty updateStatementGenerator,
            StatementGeneratorForType returnStatementGenerator) {
        this.javadoc = javadoc;
        this.updateStatementGenerator = updateStatementGenerator;
        this.returnStatementGenerator = returnStatementGenerator;
    }

    /*package*/ MethodSpec generate(SpecDesc specDesc, ImplementationDesc implementationDesc, PropertyDesc propertyDesc) {
        final String typeName = propertyDesc.getType();

        if (!COLLECTION_TYPES.contains(typeName)) {
            throw new IllegalArgumentException("The CollectionAddConfiguratorGenerator only supports collection types");
        }

        final List<String> typeParameters = propertyDesc.getTypeParameters();

        if (typeParameters.size() > 1) {
            throw new IllegalArgumentException("The CollectionAddConfiguratorGenerator does not support collections " +
                    "with multiple parameter types");
        }
        else if (typeParameters.size() < 1) {
            throw new IllegalArgumentException("The CollectionAddConfiguratorGenerator needs a type parameter.");
        }

        final TypeName elementType = getType(typeParameters.get(0));
        final String pluralPropertyName = propertyDesc.getName();
        final String propertyName = propertyDesc.getName().substring(0, pluralPropertyName.length() - 1);
        final ParameterSpec methodParameter = ParameterSpec.builder(elementType, propertyName).build();
        MethodSpec.Builder methodBuilder = methodBuilder("add" + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1))
            .addModifiers(PUBLIC)
            .addJavadoc(javadoc, propertyName)
            .returns(ClassName.get(implementationDesc.getPackageName(), implementationDesc.getName(), "Builder"))
            .addParameter(methodParameter);

        if (!elementType.isPrimitive()) {
            methodBuilder = methodBuilder
                .addStatement(
                    "$T.requireNonNull($N)",
                    ClassName.get(Objects.class),
                    methodParameter);
        }

        final Builder builder = updateStatementGenerator
            .generate(methodBuilder, specDesc, implementationDesc, propertyDesc);

        return builder
            .addStatement("return " + returnStatementGenerator.generate(implementationDesc))
            .build();
    }

    @Override
    public void append(
            TypeSpec.Builder typeSpecBuilder,
            SpecDesc specDesc,
            ImplementationDesc implementationDesc,
            PropertyDesc propertyDesc) {

        final MethodSpec methodSpec = generate(specDesc, implementationDesc, propertyDesc);
        typeSpecBuilder.addMethod(methodSpec);
    }
}
