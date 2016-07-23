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

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

/**
 * Generator for setting configurator.
 * @author Matt Champion on 24/07/2016
 */
public final class SettingConfiguratorGenerator implements MethodGeneratorForProperty {
    private final String javadoc;
    private final StatementGeneratorForType returnStatementGenerator;

    /**
     * Constructor.
     */
    public SettingConfiguratorGenerator(String javadoc, StatementGeneratorForType returnStatementGenerator) {
        this.javadoc = javadoc;
        this.returnStatementGenerator = returnStatementGenerator;
    }

    @Override
    public MethodSpec generate(SpecDesc specDesc, TypeDesc typeDesc, PropertyDesc propertyDesc) {
        final TypeName type = getType(propertyDesc);
        final ParameterSpec constructorParameter = ParameterSpec.builder(type, propertyDesc.getName()).build();
        return methodBuilder(propertyDesc.getName())
            .addModifiers(PUBLIC)
            .addJavadoc(javadoc, propertyDesc.getName())
            .returns(ClassName.get(typeDesc.getPackageName(), typeDesc.getName(), "Builder"))
            .addParameter(constructorParameter)
            .addStatement("this.$L = $N", propertyDesc.getName(), constructorParameter)
            .addStatement("return " + returnStatementGenerator.generate(typeDesc))
            .build();
    }
}
