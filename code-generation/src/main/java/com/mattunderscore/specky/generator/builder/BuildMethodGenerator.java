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

import com.mattunderscore.specky.generator.MethodGeneratorForType;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.mattunderscore.specky.javapoet.javadoc.JavaDocBuilder.docMethod;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Generator for build methods.
 * @author Matt Champion on 16/06/2016
 */
public final class BuildMethodGenerator implements MethodGeneratorForType<ImplementationDesc> {

    @Override
    public MethodSpec generate(SpecDesc specDesc, ImplementationDesc valueDesc) {
        final MethodSpec.Builder buildMethod = methodBuilder("build")
            .addModifiers(PUBLIC)
            .returns(ClassName.get(valueDesc.getPackageName(), valueDesc.getName()))
            .addJavadoc(
                docMethod()
                    .setMethodDescription("Build an instance of $1L.")
                    .setReturnsDescription("a new instance of $1L")
                    .toJavaDoc(),
                valueDesc.getName());
        addValidationStatements(buildMethod, valueDesc);
        addReturnStatement(buildMethod, valueDesc);
        return buildMethod.build();
    }

    private void addReturnStatement(MethodSpec.Builder buildMethod, ImplementationDesc valueDesc) {
        final List<CodeBlock> codeBlocks = valueDesc
            .getProperties()
            .stream()
            .map(BuildMethodGenerator::get)
            .collect(Collectors.toList());

        final CodeBlock.Builder statementBuilder = CodeBlock
            .builder()
            .add("$[return new $T(", ClassName.get(valueDesc.getPackageName(), valueDesc.getName()));
        final Iterator<CodeBlock> iterator = codeBlocks.iterator();
        while (iterator.hasNext()) {
            statementBuilder.add(iterator.next());
            if (iterator.hasNext()) {
                statementBuilder.add(", ");
            }
        }

        buildMethod.addCode(statementBuilder.add(");$]").build());
    }

    private void addValidationStatements(MethodSpec.Builder methodSpecBuilder, ImplementationDesc valueDesc) {
        valueDesc
            .getProperties()
            .stream()
            .forEach(propertySpec -> {
                final TypeName typeName = getType(propertySpec);
                if (!propertySpec.isOptional() && !typeName.isPrimitive()) {
                    methodSpecBuilder
                        .addStatement("$T.requireNonNull($N)", ClassName.get(Objects.class), propertySpec.getName());
                }
            });
    }

    private static CodeBlock get(PropertyDesc propertyDesc) {
        if ("java.util.Set".equals(propertyDesc.getType())) {
            return getWrappedCollection(propertyDesc, "unmodifiableSet");
        }
        else if ("java.util.List".equals(propertyDesc.getType())) {
            return getWrappedCollection(propertyDesc, "unmodifiableList");
        }
        else {
            return CodeBlock.of(propertyDesc.getName());
        }
    }

    private static CodeBlock getWrappedCollection(PropertyDesc propertyDesc, String wrapper) {
        if (propertyDesc.isOptional()) {
            return CodeBlock.of(
                "$L != null ? $T.$L($L) : null",
                propertyDesc.getName(),
                Collections.class,
                wrapper,
                propertyDesc.getName());
        }
        else {
            return CodeBlock.of(
                "$T.$L($L)",
                Collections.class,
                wrapper,
                propertyDesc.getName());
        }
    }
}
