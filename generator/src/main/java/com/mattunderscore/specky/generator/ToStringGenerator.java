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

import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Generator for toString implementation.
 * @author Matt Champion on 27/06/16
 */
public final class ToStringGenerator {
    public static final ToStringGenerator SQUARE_BRACKET_COMMA_AND_SPACE_SEPARATED =
            new ToStringGenerator("[", "]", ", ", "%1$s=\" + %1$s + \"");
    public static final ToStringGenerator ROUND_BRACKET_COMMA_AND_SPACE_SEPARATED =
            new ToStringGenerator("(", ")", ", ", "%1$s=\" + %1$s + \"");
    public static final ToStringGenerator SQUARE_BRACKET_COMMA_SEPARATED =
            new ToStringGenerator("[", "]", ",", "%1$s=\" + %1$s + \"");
    public static final ToStringGenerator ROUND_BRACKET_COMMA_SEPARATED =
            new ToStringGenerator("(", ")", ",", "%1$s=\" + %1$s + \"");

    private final String propertyListPrefix;
    private final String propertyListSuffix;
    private final String propertySeparator;
    private final String propertyFormat;

    private ToStringGenerator(
            String propertyListPrefix,
            String propertyListSuffix,
            String propertySeparator,
            String propertyFormat) {

        this.propertyListPrefix = propertyListPrefix;
        this.propertyListSuffix = propertyListSuffix;
        this.propertySeparator = propertySeparator;
        this.propertyFormat = propertyFormat;
    }

    public MethodSpec generate(TypeDesc typeDesc) {
        return methodBuilder("toString")
                .returns(ClassName.get(String.class))
                .addModifiers(PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Override.class).build())
                .addCode(generateImplementation(typeDesc))
                .build();
    }

    private CodeBlock generateImplementation(TypeDesc typeDesc) {
        final String properties = typeDesc
                .getProperties()
                .stream()
                .map(this::formatProperty)
                .collect(joining(propertySeparator));
        return CodeBlock
                .builder()
                .addStatement(
                        "return \"$L$L$L$L\"",
                        typeDesc.getName(),
                        propertyListPrefix,
                        properties,
                        propertyListSuffix)
                .build();
    }

    private String formatProperty(PropertyDesc propertyDesc) {
        return format(propertyFormat, propertyDesc.getName());
    }
}
