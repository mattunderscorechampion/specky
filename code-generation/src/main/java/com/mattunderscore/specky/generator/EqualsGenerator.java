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

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeName.OBJECT;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.stream.Collectors;

import com.mattunderscore.specky.model.PropertyImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

/**
 * Equals method generator.
 * @author Matt Champion on 06/07/2016
 */
public final class EqualsGenerator implements MethodGeneratorForType {
    private final ParameterSpec other = ParameterSpec.builder(OBJECT, "other").build();

    @Override
    public MethodSpec generate(SpecDesc specDesc, TypeDesc typeDesc) {
        return methodBuilder("equals")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .addParameter(other)
            .returns(TypeName.BOOLEAN)
            .addCode(generateBlock(specDesc, typeDesc))
            .build();
    }

    private CodeBlock generateBlock(SpecDesc specDesc, TypeDesc typeDesc) {
        final CodeBlock.Builder codeBlock = CodeBlock
            .builder()
            .beginControlFlow("if ($N == this)", other)
            .addStatement("return true")
            .endControlFlow()
            .beginControlFlow("else if ($1N == null || !this.getClass().equals($1N.getClass()))", other)
            .addStatement("return false")
            .endControlFlow()
            .addStatement(
                "final $1T that = ($1T) $2N",
                ClassName.get(typeDesc.getPackageName(), typeDesc.getName()), other)
            .addStatement("return " + typeDesc
                .getProperties()
                .stream()
                .map(this::generatePropertyComparison)
                .collect(Collectors.joining(" && ")));

        return codeBlock.build();
    }

    private String generatePropertyComparison(PropertyImplementationDesc propertyImplementationDesc) {
        final String name = propertyImplementationDesc.getName();
        final String type = propertyImplementationDesc.getType();

        if ("int".equals(type) || "boolean".equals(type) || "double".equals(type) || "long".equals(type)) {
            return "this." + name + " == " + "that." + name;
        }

        if (propertyImplementationDesc.isOptional()) {
            return "(this." +
                name +
                " == null && that." +
                name +
                " == null || (" +
                "this." +
                name +
                " != null && this." +
                name +
                ".equals(that." +
                name +
                ")))";
        }

        return "this." + name + ".equals(that." + name +")";
    }
}
