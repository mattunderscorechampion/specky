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

package com.mattunderscore.specky.generator.object.method;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.mattunderscore.specky.generator.TypeAppender;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for toString implementation.
 * @author Matt Champion on 27/06/16
 */
public final class ToStringGenerator implements TypeAppender<ImplementationDesc> {
    /**
     * Comma and space separator.
     */
    public static final String COMMA_AND_SPACE_SEPARATOR = ", ";
    /**
     * Comma separator.
     */
    public static final String COMMA_SEPARATOR = ",";
    /**
     * Square brackets.
     */
    public static final PropertyListBookend SQUARE_BRACKETS = new SquareBrackets();
    /**
     * Round brackets.
     */
    public static final PropertyListBookend ROUND_BRACKETS = new RoundBrackets();
    /**
     * Property formatter.
     */
    public static final SimplePropertyFormatter SIMPLE_PROPERTY_FORMATTER = new SimplePropertyFormatter();

    private final PropertyListBookend propertyListBookend;
    private final String propertySeparator;
    private final PropertyFormatter propertyFormatter;

    /**
     * Constructor.
     */
    public ToStringGenerator(
            PropertyListBookend propertyListBookend,
            String propertySeparator,
            PropertyFormatter propertyFormatter) {

        this.propertyListBookend = propertyListBookend;
        this.propertySeparator = propertySeparator;
        this.propertyFormatter = propertyFormatter;
    }

    @Override
    public void append(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, ImplementationDesc typeDesc) {
        final MethodSpec methodSpec = methodBuilder("toString")
            .returns(ClassName.get(String.class))
            .addModifiers(PUBLIC)
            .addAnnotation(AnnotationSpec.builder(Override.class).build())
            .addCode(generateImplementation(typeDesc))
            .build();
        typeSpecBuilder.addMethod(methodSpec);
    }

    private CodeBlock generateImplementation(ImplementationDesc implementationDesc) {
        if (implementationDesc.getProperties().size() > 0) {
            final String properties = implementationDesc
                .getProperties()
                .stream()
                .map(propertyFormatter::formatProperty)
                .collect(joining(propertySeparator));
            return CodeBlock
                .builder()
                .addStatement(
                    "return \"$L$L$L$L\"",
                    implementationDesc.getName(),
                    propertyListBookend.getPrefix(),
                    properties,
                    propertyListBookend.getSuffix())
                .build();
        }
        else {
            return CodeBlock.of("return $S;", implementationDesc.getName());
        }
    }

    /**
     * Property formatter.
     */
    public interface PropertyFormatter {
        /**
         * Generate the code for the property. Starts within a string literal.
         */
        String formatProperty(PropertyDesc propertyDesc);
    }

    /**
     * Describe the start and end of a property list.
     */
    public interface PropertyListBookend {
        /**
         * @return the prefix, starts within a string literal
         */
        String getPrefix();

        /**
         * @return the suffix, starts within a string literal
         */
        String getSuffix();
    }

    private static final class SimplePropertyFormatter implements PropertyFormatter {
        @Override
        public String formatProperty(PropertyDesc propertyDesc) {
            return format("%1$s=\" + %1$s + \"", propertyDesc.getName());
        }
    }

    private static final class RoundBrackets implements PropertyListBookend {
        @Override
        public String getPrefix() {
            return "(";
        }

        @Override
        public String getSuffix() {
            return ")";
        }
    }

    private static final class SquareBrackets implements PropertyListBookend {
        @Override
        public String getPrefix() {
            return "[";
        }

        @Override
        public String getSuffix() {
            return "]";
        }
    }
}
