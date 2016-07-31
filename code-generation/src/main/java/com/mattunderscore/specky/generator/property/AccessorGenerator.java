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
import static com.mattunderscore.specky.javapoet.javadoc.JavaDocBuilder.docMethod;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.lang.Character.toUpperCase;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.mattunderscore.specky.generator.MethodGeneratorForProperty;
import com.mattunderscore.specky.javapoet.javadoc.JavaDocMethodBuilder;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.MethodSpec;

/**
 * Generator for accessor method.
 * @author Matt Champion on 21/06/2016
 */
public final class AccessorGenerator implements MethodGeneratorForProperty {
    /**
     * Constructor.
     */
    public AccessorGenerator() {
    }

    @Override
    public MethodSpec generate(SpecDesc specDesc, TypeDesc typeDesc, PropertyDesc propertyDesc) {
        if (propertyDesc.isOverride()) {
            return methodBuilder(getAccessorName(propertyDesc))
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class)
                .returns(getType(propertyDesc))
                .addStatement("return $N", propertyDesc.getName())
                .build();
        }
        else {
            final JavaDocMethodBuilder javaDocMethodBuilder = docMethod();

            if (propertyDesc.getDescription() == null || "".equals(propertyDesc.getDescription())) {
                javaDocMethodBuilder.setMethodDescription("Getter for the property $1L.");
            }
            else {
                javaDocMethodBuilder.setMethodDescription(propertyDesc.getDescription());
            }

            return methodBuilder(getAccessorName(propertyDesc))
                .addModifiers(PUBLIC)
                .addJavadoc(
                    javaDocMethodBuilder
                        .setReturnsDescription("the value of $1L")
                        .toJavaDoc(),
                    propertyDesc.getName())
                .returns(getType(propertyDesc))
                .addStatement("return $N", propertyDesc.getName())
                .build();
        }
    }

    private static String getAccessorName(PropertyDesc property) {
        final String propertyName = property.getName();
        if (property.getTypeName().equals("boolean")) {
            return "is" + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        }
        else {
            return "get" + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        }
    }
}