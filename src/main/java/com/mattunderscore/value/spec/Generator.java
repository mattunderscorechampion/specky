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

package com.mattunderscore.value.spec;

import com.mattunderscore.value.spec.model.SpecDesc;
import com.mattunderscore.value.spec.model.ValueDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.stream.Collectors;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Code generator for specification.
 * @author Matt Champion on 05/06/16
 */
public final class Generator {
    public List<JavaFile> generate(SpecDesc specDesc) {
        return specDesc
            .getValues()
            .stream()
            .map(this::generateType)
            .map(typeSpec -> JavaFile.builder(specDesc.getPackageName(), typeSpec).build())
            .collect(Collectors.toList());
    }

    private TypeSpec generateType(ValueDesc valueDesc) {
        final TypeSpec.Builder builder = TypeSpec
            .classBuilder(valueDesc.getName())
            .addModifiers(PUBLIC, FINAL)
            .addJavadoc("Value type $L.\n\nAuto-generated from specification.\n", valueDesc.getName());
        final MethodSpec.Builder constructor = MethodSpec.constructorBuilder().addModifiers(PUBLIC);
        valueDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final ClassName type = ClassName.bestGuess(propertyDesc.getType());
                final FieldSpec fieldSpec = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE, FINAL).build();
                final MethodSpec methodSpec = MethodSpec.methodBuilder("get" + propertyDesc.getName())
                    .addModifiers(PUBLIC)
                    .addJavadoc("Getter for the property $L\n@returns the value of $L\n", propertyDesc.getName(), propertyDesc.getName())
                    .returns(type)
                    .addStatement("return $N", fieldSpec)
                    .build();
                final ParameterSpec constructorParameter = ParameterSpec.builder(type, propertyDesc.getName()).build();

                constructor.addParameter(constructorParameter);
                constructor.addStatement("this.$N = $N", fieldSpec, constructorParameter);

                builder
                    .addField(fieldSpec)
                    .addMethod(methodSpec);
            });
        return builder.addMethod(constructor.build()).build();
    }
}
