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

import static com.mattunderscore.specky.javapoet.javadoc.JavaDocBuilder.docMethod;
import static com.mattunderscore.specky.javapoet.javadoc.JavaDocBuilder.docType;
import static java.lang.Character.toUpperCase;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * @author Matt Champion on 11/06/2016
 */
/*package*/ final class GeneratorUtils {
    public final static String CONSTRUCTOR_DOC = docMethod()
        .setMethodDescription("Constructor.")
        .toJavaDoc();
    public final static String GETTER_DOC = docMethod()
        .setMethodDescription("Getter for the property $1L.")
        .setReturnsDescription("the value of $1L")
        .toJavaDoc();
    public final static String SETTER_DOC = docMethod()
        .setMethodDescription("Setter for the property $1L.")
        .addParameter("$2L", "the new value of $1L")
        .toJavaDoc();
    public final static String TYPE_DOC = docType()
        .setDescription("$L type $L.\n\nAuto-generated from specification.")
        .toJavaDoc();
    public final static String BUILDER_TYPE_DOC = docType()
        .setDescription("The builder for $L.")
        .toJavaDoc();
    public final static String BUILD_DOC = docMethod()
        .setMethodDescription("Build an instance of $1L.")
        .setReturnsDescription("a new instance of $1L")
        .toJavaDoc();
    public final static String BUILDER_FACTORY = docMethod()
        .setMethodDescription("Factory method for builder.")
        .setReturnsDescription("a new builder for $L")
        .toJavaDoc();
    public final static String MUTABLE_BUILDER_SETTER = docMethod()
        .setMethodDescription("Method to configure property $L on the builder.")
        .setReturnsDescription("this builder")
        .toJavaDoc();
    public final static String IMMUTABLE_BUILDER_SETTER = docMethod()
        .setMethodDescription("Method to configure property $L on the builder.")
        .setReturnsDescription("a new builder")
        .toJavaDoc();

    private GeneratorUtils() {
    }

    public static TypeName getType(String type) {

        if ("int".equals(type)) {
            return TypeName.INT;
        }
        else if ("boolean".equals(type)) {
            return TypeName.BOOLEAN;
        }
        else if ("double".equals(type)) {
            return TypeName.DOUBLE;
        }

        return ClassName.bestGuess(type);
    }
}
