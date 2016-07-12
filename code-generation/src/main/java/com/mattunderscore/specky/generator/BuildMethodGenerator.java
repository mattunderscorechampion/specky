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

import static com.mattunderscore.specky.generator.GeneratorUtils.BUILD_DOC;
import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.Objects;
import java.util.stream.Collectors;

import com.mattunderscore.specky.dsl.model.PropertyImplementationDesc;
import com.mattunderscore.specky.dsl.model.SpecDesc;
import com.mattunderscore.specky.dsl.model.TypeDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

/**
 * @author Matt Champion on 16/06/2016
 */
public final class BuildMethodGenerator implements MethodGeneratorForType {

    @Override
    public MethodSpec generate(SpecDesc specDesc, TypeDesc valueDesc) {
        final MethodSpec.Builder buildMethod = methodBuilder("build")
            .addModifiers(PUBLIC)
            .returns(ClassName.get(specDesc.getPackageName(), valueDesc.getName()))
            .addJavadoc(BUILD_DOC, valueDesc.getName());
        addValidationStatements(buildMethod, valueDesc);
        addReturnStatement(buildMethod, specDesc, valueDesc);
        return buildMethod.build();
    }

    private void addReturnStatement(MethodSpec.Builder buildMethod, SpecDesc specDesc, TypeDesc valueDesc) {
        buildMethod.addStatement(
            "return new $T(" +
            valueDesc
                .getProperties()
                .stream()
                .map(PropertyImplementationDesc::getName)
                .collect(Collectors.joining(", ")) +
            ')',
            ClassName.get(specDesc.getPackageName(), valueDesc.getName()));
    }

    private void addValidationStatements(MethodSpec.Builder methodSpecBuilder, TypeDesc valueDesc) {
        valueDesc
            .getProperties()
            .stream()
            .forEach(propertySpec -> {
                final TypeName typeName = getType(propertySpec.getType());
                if (!propertySpec.isOptional() && !typeName.isPrimitive()) {
                    methodSpecBuilder.addStatement("$T.requireNonNull($N)", ClassName.get(Objects.class), propertySpec.getName());
                }
            });
    }
}
