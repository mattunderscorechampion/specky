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

import static com.mattunderscore.specky.generator.GeneratorUtils.SETTER_DOC;
import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.lang.Character.toUpperCase;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.Objects;

import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

/**
 * Generator for mutator method.
 * @author Matt Champion on 21/06/2016
 */
public final class MutatorGenerator implements MethodGeneratorForProperty {
    @Override
    public MethodSpec generate(SpecDesc specDesc, TypeDesc typeDesc, PropertyDesc propertyDesc) {
        final TypeName type = getType(propertyDesc);
        final ParameterSpec parameterSpec = ParameterSpec.builder(type, propertyDesc.getName()).build();
        final MethodSpec.Builder setterSpec = methodBuilder(getMutatorName(propertyDesc.getName()))
            .addModifiers(PUBLIC)
            .addParameter(parameterSpec)
            .addJavadoc(SETTER_DOC, propertyDesc.getName(), propertyDesc.getName())
            .returns(TypeName.VOID);

        if (!propertyDesc.isOptional() && !type.isPrimitive()) {
            setterSpec.addStatement("$T.requireNonNull($N)", ClassName.get(Objects.class), propertyDesc.getName());
        }

        setterSpec.addStatement("this.$N = $N", propertyDesc.getName(), parameterSpec);

        return setterSpec.build();
    }

    private static String getMutatorName(String propertyName) {
        return "set" + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }
}
