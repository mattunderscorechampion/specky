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

import static javax.lang.model.element.Modifier.PRIVATE;

import javax.lang.model.element.Modifier;

import com.mattunderscore.specky.generator.constructor.AllPropertiesConstructorGenerator;
import com.mattunderscore.specky.generator.constructor.DefaultConstructorGenerator;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Appender for a defaults static method.
 * @author Matt Champion on 30/10/2016
 */
public final class DefaultsGenerator implements TypeAppender {
    private final MethodGeneratorForType defaultConstructorGenerator = new DefaultConstructorGenerator(PRIVATE);
    private final MethodGeneratorForType allConstructorGenerator = new AllPropertiesConstructorGenerator(PRIVATE);

    @Override
    public void append(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, ImplementationDesc valueDesc) {
        typeSpecBuilder.addMethod(defaultConstructorGenerator.generate(specDesc, valueDesc));
        typeSpecBuilder.addMethod(allConstructorGenerator.generate(specDesc, valueDesc));

        typeSpecBuilder
            .addMethod(MethodSpec
                .methodBuilder("defaults")
                .addJavadoc("Return an instance with default values.\n")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .returns(ClassName.get(valueDesc.getPackageName(), valueDesc.getName()))
                .addStatement("return new " + valueDesc.getName() + "()")
                .build());
    }
}
