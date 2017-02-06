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

import static com.squareup.javapoet.ParameterizedTypeName.get;

import java.util.function.Function;

import javax.lang.model.element.Modifier;

import com.mattunderscore.specky.generator.TypeAppender;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for functional configurators.
 *
 * @author Matt Champion on 15/09/2016
 */
public final class FunctionalConfiguratorGenerator implements TypeAppender<ImplementationDesc> {
    private final String javaDoc;

    /**
     * Constructor.
     */
    public FunctionalConfiguratorGenerator(String javaDoc) {
        this.javaDoc = javaDoc;
    }

    @Override
    public void append(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, ImplementationDesc typeDesc) {
        final ClassName builderType = ClassName.get(typeDesc.getPackageName(), typeDesc.getName(), "Builder");
        final ParameterSpec functionParameter = ParameterSpec
            .builder(get(ClassName.get(Function.class), builderType, builderType), "function")
            .build();
        final MethodSpec methodSpec = MethodSpec
            .methodBuilder("apply")
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc(javaDoc)
            .returns(builderType)
            .addParameter(functionParameter)
            .addCode(CodeBlock
                .builder()
                .addStatement("return $N.apply(this)", functionParameter)
                .build())
            .build();
        typeSpecBuilder.addMethod(methodSpec);
    }
}
