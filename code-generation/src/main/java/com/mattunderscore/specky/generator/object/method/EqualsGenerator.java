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

package com.mattunderscore.specky.generator.object.method;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeName.OBJECT;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.stream.Collectors;

import com.mattunderscore.specky.generator.TypeAppender;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Equals method generator.
 * @author Matt Champion on 06/07/2016
 */
public final class EqualsGenerator implements TypeAppender<ImplementationDesc> {
    private final ParameterSpec other = ParameterSpec.builder(OBJECT, "other").build();

    private CodeBlock generateBlock(ImplementationDesc implementationDesc) {
        final CodeBlock.Builder codeBlock = CodeBlock
            .builder()
            .beginControlFlow("if ($N == this)", other)
            .addStatement("return true")
            .endControlFlow()
            .beginControlFlow("else if ($1N == null || !this.getClass().equals($1N.getClass()))", other)
            .addStatement("return false")
            .endControlFlow();

            if (implementationDesc.getProperties().size() > 0) {
                codeBlock
                    .addStatement(
                        "final $1T that = ($1T) $2N",
                        ClassName.get(implementationDesc.getPackageName(), implementationDesc.getName()), other)
                    .addStatement("return " + implementationDesc
                        .getProperties()
                        .stream()
                        .map(this::generatePropertyComparison)
                        .collect(Collectors.joining(" && ")));
            }
            else {
                codeBlock.addStatement("return true");
            }

        return codeBlock.build();
    }

    private String generatePropertyComparison(PropertyDesc propertyDesc) {
        final String name = propertyDesc.getName();
        final String type = propertyDesc.getType();

        if ("int".equals(type) || "boolean".equals(type) || "double".equals(type) || "long".equals(type)) {
            return "this." + name + " == " + "that." + name;
        }

        if (propertyDesc.isOptional()) {
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

        return "this." + name + ".equals(that." + name + ")";
    }

    @Override
    public void append(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, ImplementationDesc typeDesc) {
        final MethodSpec methodSpec = methodBuilder("equals")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .addParameter(other)
            .returns(TypeName.BOOLEAN)
            .addCode(generateBlock(typeDesc))
            .build();
        typeSpecBuilder.addMethod(methodSpec);
    }
}
