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

import static com.mattunderscore.specky.generator.GeneratorUtils.getAccessorJavadoc;
import static com.mattunderscore.specky.generator.GeneratorUtils.getAccessorName;
import static com.mattunderscore.specky.generator.GeneratorUtils.getTypeJavadoc;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.mattunderscore.specky.model.ConstructionDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * @author Matt Champion on 11/06/2016
 */
/*package*/ final class ValueGenerator {
    private ValueGenerator() {
    }

    static TypeSpec generateValue(SpecDesc specDesc, ValueDesc valueDesc) {
        final TypeSpec.Builder builder = TypeSpec
            .classBuilder(valueDesc.getName())
            .addModifiers(PUBLIC, FINAL)
            .addJavadoc(getTypeJavadoc(), "Value", valueDesc.getName());

        if (valueDesc.getConstruction() == ConstructionDesc.CONSTRUCTOR) {
            ConstructorGenerator.build(builder, valueDesc);
        }
        else if (valueDesc.getConstruction() == ConstructionDesc.MUTABLE_BUILDER) {
            MutableBuilderGenerator.build(builder, specDesc, valueDesc);
        }
        else if (valueDesc.getConstruction() == ConstructionDesc.IMMUTABLE_BUILDER) {
            ImmutableBuilderGenerator.build(builder, specDesc, valueDesc);
        }
        else {
            throw new IllegalArgumentException("Unsupported construction type");
        }

        valueDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final ClassName type = ClassName.bestGuess(propertyDesc.getType());
                final FieldSpec fieldSpec = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE, FINAL).build();
                final MethodSpec methodSpec = methodBuilder(getAccessorName(propertyDesc.getName()))
                    .addModifiers(PUBLIC)
                    .addJavadoc(getAccessorJavadoc(), propertyDesc.getName(), propertyDesc.getName())
                    .returns(type)
                    .addStatement("return $N", fieldSpec)
                    .build();

                builder
                    .addField(fieldSpec)
                    .addMethod(methodSpec);
            });

        return builder.build();
    }
}
