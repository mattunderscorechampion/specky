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

package com.mattunderscore.specky.generator.property;

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.lang.Character.toUpperCase;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.Objects;

import com.mattunderscore.specky.generator.TypeAppenderForProperty;
import com.mattunderscore.specky.generator.constraint.PropertyConstraintGenerator;
import com.mattunderscore.specky.generator.statements.StatementGeneratorForType;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for with modifier method.
 * @author Matt Champion on 29/10/2016
 */
public final class WithModifierGenerator implements TypeAppenderForProperty<ImplementationDesc> {
    private final String javadoc;
    private final StatementGeneratorForType returnStatementGenerator;
    private final PropertyConstraintGenerator propertyConstraintGenerator = new PropertyConstraintGenerator();

    /**
     * Constructor.
     */
    public WithModifierGenerator(String javadoc, StatementGeneratorForType returnStatementGenerator) {
        this.javadoc = javadoc;
        this.returnStatementGenerator = returnStatementGenerator;
    }

    /*package*/ MethodSpec generate(SpecDesc specDesc, ImplementationDesc implementationDesc, PropertyDesc propertyDesc) {
        if (!implementationDesc.isWithModification()) {
            return null;
        }

        final TypeName type = getType(propertyDesc);
        final String propertyName = propertyDesc.getName();
        final ParameterSpec constructorParameter = ParameterSpec.builder(type, propertyName).build();
        MethodSpec.Builder methodBuilder = methodBuilder("with" + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1))
            .addModifiers(PUBLIC)
            .addJavadoc(javadoc, propertyName)
            .returns(ClassName.get(implementationDesc.getPackageName(), implementationDesc.getName()))
            .addParameter(constructorParameter);

        if (!propertyDesc.isOptional() && !type.isPrimitive()) {
            methodBuilder = methodBuilder
                .addStatement(
                    "$T.requireNonNull($N)",
                    ClassName.get(Objects.class),
                    constructorParameter);
        }

        if (propertyDesc.getConstraint() != null) {
            methodBuilder.addCode(propertyConstraintGenerator.generate(propertyDesc));
        }

        return methodBuilder
            .addStatement("return " + returnStatementGenerator.generate(implementationDesc))
            .build();
    }

    @Override
    public void append(
            TypeSpec.Builder typeSpecBuilder,
            SpecDesc specDesc,
            ImplementationDesc implementationDesc,
            PropertyDesc propertyDesc) {

        final MethodSpec methodSpec = generate(specDesc, implementationDesc, propertyDesc);
        if (methodSpec != null) {
            typeSpecBuilder.addMethod(methodSpec);
        }
    }
}
