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

import java.util.List;
import java.util.stream.Collectors;

import com.mattunderscore.specky.model.PropertyDesc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

/**
 * Generator utilities.
 * @author Matt Champion on 11/06/2016
 */
public final class GeneratorUtils {

    private GeneratorUtils() {
    }

    /**
     * @return the type name of a property
     */
    public static TypeName getType(PropertyDesc property) {
        final List<String> typeParameters = property.getTypeParameters();
        if (typeParameters.size() > 0) {
            return getType(property.getType(), typeParameters);
        }
        else {
            return getType(property.getType());
        }
    }

    /**
     * @return the type name
     */
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
        else if ("long".equals(type)) {
            return TypeName.LONG;
        }

        return ClassName.bestGuess(type);
    }

    /**
     * @return the type name with parameters
     */
    public static ParameterizedTypeName getType(String type, List<String> typeParameters) {
        final List<TypeName> parameterList = typeParameters.stream().map(GeneratorUtils::getType).collect(Collectors.toList());
        final TypeName[] parameters = new TypeName[parameterList.size()];
        parameterList.toArray(parameters);

        final ClassName mainType = ClassName.bestGuess(type);
        return ParameterizedTypeName.get(mainType, parameters);
    }
}
