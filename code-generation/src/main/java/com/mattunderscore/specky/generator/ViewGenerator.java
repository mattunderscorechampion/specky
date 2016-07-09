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

import static com.mattunderscore.specky.generator.GeneratorUtils.GETTER_DOC;
import static com.mattunderscore.specky.generator.GeneratorUtils.TYPE_DOC;
import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.interfaceBuilder;
import static java.lang.Character.toUpperCase;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.ViewDesc;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for views.
 * @author Matt Champion on 25/06/2016
 */
public final class ViewGenerator {
    public TypeSpec generateView(SpecDesc specDesc, ViewDesc typeDesc) {
        final TypeSpec.Builder builder = interfaceBuilder(typeDesc.getName())
            .addJavadoc(TYPE_DOC, "View", typeDesc.getName());

        for (PropertyDesc view : typeDesc.getProperties()) {
            builder
                .addMethod(methodBuilder(getAccessorName(view.getName()))
                    .addJavadoc(GETTER_DOC, view.getName())
                    .addModifiers(ABSTRACT, PUBLIC)
                    .returns(getType(view.getType()))
                    .build());
        }

        return builder.build();
    }

    private static String getAccessorName(String propertyName) {
        return "get" + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }
}