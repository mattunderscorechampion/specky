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

import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;

/**
 * Generate a statement to update a collection.
 *
 * @author Matt Champion on 16/09/16
 */
public final class NewModifiedCollection implements StatementGeneratorForProperty {
    @Override
    public String generate(SpecDesc specDesc, ImplementationDesc implementationDesc, PropertyDesc propertyDesc) {
        if ("java.util.Set".equals(propertyDesc.getType())) {
            return generateSet(propertyDesc);
        }
        else if ("java.util.List".equals(propertyDesc.getType())) {
            return generateList(propertyDesc);
        }
        else {
            throw new IllegalArgumentException("Type " + propertyDesc.getType() + " not supported");
        }
    }

    private String generateList(PropertyDesc propertyDesc) {
        final String pluralPropertyName = propertyDesc.getName();
        final String propertyName = propertyDesc.getName().substring(0, pluralPropertyName.length() - 1);
        final TypeName elementType = getType(propertyDesc.getTypeParameters().get(0));

        return CodeBlock
            .builder()
            .addStatement("final List<" + elementType + ">" + pluralPropertyName + " = new java.util.ArrayList<>()")
            .beginControlFlow("for (" + elementType + " temp : this." + pluralPropertyName + ")")
            .addStatement(pluralPropertyName + ".add(temp)")
            .endControlFlow()
            .addStatement(pluralPropertyName + ".add(" + propertyName + ")")
            .build()
            .toString();
    }

    private String generateSet(PropertyDesc propertyDesc) {
        final String pluralPropertyName = propertyDesc.getName();
        final String propertyName = propertyDesc.getName().substring(0, pluralPropertyName.length() - 1);
        final TypeName elementType = getType(propertyDesc.getTypeParameters().get(0));

        return CodeBlock
            .builder()
            .addStatement("final Set<" + elementType + ">" + pluralPropertyName + " = new java.util.HashSet<>()")
            .beginControlFlow("for (" + elementType + " temp : this." + pluralPropertyName + ")")
            .addStatement(pluralPropertyName + ".add(temp)")
            .endControlFlow()
            .addStatement(pluralPropertyName + ".add(" + propertyName + ")")
            .build()
            .toString();
    }
}
