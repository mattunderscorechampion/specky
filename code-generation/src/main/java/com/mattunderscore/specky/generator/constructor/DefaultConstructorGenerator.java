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

import javax.lang.model.element.Modifier;

import com.mattunderscore.specky.generator.MethodGeneratorForType;
import com.mattunderscore.specky.generator.constraint.PropertyConstraintGenerator;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

/**
 * Default constructor generator.
 * @author Matt Champion on 13/06/2016
 */
public final class DefaultConstructorGenerator implements MethodGeneratorForType<ImplementationDesc> {
    private final Modifier constructorAccessability;
    private final PropertyConstraintGenerator propertyConstraintGenerator = new PropertyConstraintGenerator();

    /**
     * Constructor.
     */
    public DefaultConstructorGenerator(Modifier constructorAccessability) {
        this.constructorAccessability = constructorAccessability;
    }

    @Override
    public MethodSpec generate(SpecDesc specDesc, ImplementationDesc implementationDesc) {
        final MethodSpec.Builder constructor = constructorBuilder()
            .addModifiers(constructorAccessability)
            .addJavadoc(docMethod()
                .setMethodDescription("Default constructor.")
                .toJavaDoc());

        implementationDesc
            .getProperties()
            .stream()
            .forEach(propertyDesc -> {
                final TypeName type = getType(propertyDesc);
                final FieldSpec fieldSpec = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE, FINAL).build();

                final CodeBlock defaultValue = propertyDesc.getDefaultValue();

                if (defaultValue == null) {
                    if (propertyDesc.getConstraint() != null) {
                        constructor.addCode(propertyConstraintGenerator.generate(propertyDesc));
                    }

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

                    if (propertyDesc.getConstraint() != null) {
                        constructor.addCode(propertyConstraintGenerator.generate(propertyDesc));
                    }
                }
            });

        return constructor.build();
    }
}
