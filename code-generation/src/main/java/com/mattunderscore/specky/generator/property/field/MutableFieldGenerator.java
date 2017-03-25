/* Copyright © 2016-2017 Matthew Champion
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

package com.mattunderscore.specky.generator.property.field;

import static com.mattunderscore.specky.generator.GeneratorUtils.getType;
import static com.mattunderscore.specky.model.ConstructionMethod.CONSTRUCTOR;
import static javax.lang.model.element.Modifier.PRIVATE;

import com.mattunderscore.specky.generator.LiteralValueGenerator;
import com.mattunderscore.specky.generator.TypeAppenderForProperty;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for immutable fields.
 * @author Matt Champion on 07/12/2016
 */
public final class MutableFieldGenerator implements TypeAppenderForProperty<ImplementationDesc> {
    private final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

    /*package*/ FieldSpec generate(
            SpecDesc specDesc,
            ImplementationDesc implementationDesc,
            PropertyDesc propertyDesc) {

        final TypeName type = getType(propertyDesc);
        final FieldSpec.Builder builder = FieldSpec.builder(type, propertyDesc.getName(), PRIVATE);

        if (implementationDesc.getConstructionMethod() == CONSTRUCTOR && propertyDesc.getDefaultValue() != null) {
            builder.initializer(literalValueGenerator.generate(propertyDesc.getDefaultValue()));
        }

        return builder.build();
    }

    @Override
    public void append(
            TypeSpec.Builder typeSpecBuilder,
            SpecDesc specDesc,
            ImplementationDesc implementationDesc,
            PropertyDesc propertyDesc) {

        typeSpecBuilder.addField(generate(specDesc, implementationDesc, propertyDesc));
    }
}
