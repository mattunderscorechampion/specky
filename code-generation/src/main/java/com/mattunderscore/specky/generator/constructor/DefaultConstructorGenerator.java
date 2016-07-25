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

package com.mattunderscore.specky.generator.constructor;

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.mattunderscore.specky.javapoet.javadoc.JavaDocBuilder.docMethod;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.mattunderscore.specky.generator.MethodGeneratorForType;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

/**
 * Default constructor generator.
 * @author Matt Champion on 13/06/2016
 */
public final class DefaultConstructorGenerator implements MethodGeneratorForType {

    @Override
    public MethodSpec generate(SpecDesc specDesc, TypeDesc typeDesc) {
        final MethodSpec.Builder constructor = constructorBuilder()
            .addModifiers(PUBLIC)
            .addJavadoc(docMethod()
                .setMethodDescription("Default constructor.")
                .toJavaDoc());

        typeDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final TypeName type = getType(propertyDesc);
                final FieldSpec fieldSpec = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE, FINAL).build();

                final String defaultValue = propertyDesc.getDefaultValue();

                if (defaultValue == null) {
                    // If the property does not have a default value add a parameter
                    final ParameterSpec constructorParameter = ParameterSpec.builder(type, propertyDesc.getName()).build();
                    constructor
                        .addParameter(constructorParameter)
                        .addStatement("this.$N = $N", fieldSpec, constructorParameter);
                }
                else {
                    // If the property has a default value use it
                    constructor
                        .addStatement("this.$N = $L", fieldSpec, defaultValue);
                }
            });

        return constructor.build();
    }
}