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

import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.ImplementationDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.squareup.javapoet.TypeSpec;

/**
 * Appender for the construction method.
 * @author Matt Champion on 10/07/2016
 */
public final class ConstructionMethodAppender implements TypeAppender<ImplementationDesc> {
    private final TypeAppender<ImplementationDesc> constructorGenerator;
    private final TypeAppender<ImplementationDesc> defaultConstructorGenerator;
    private final TypeAppender<ImplementationDesc> mutableBuilderAppender;
    private final TypeAppender<ImplementationDesc> immutableBuilderAppender;
    private final TypeAppender<ImplementationDesc> defaultsAppender;

    /**
     * Constructor.
     */
    public ConstructionMethodAppender(
            TypeAppender<ImplementationDesc> constructorGenerator,
            TypeAppender<ImplementationDesc> defaultConstructorGenerator,
            TypeAppender<ImplementationDesc> mutableBuilderAppender,
            TypeAppender<ImplementationDesc> immutableBuilderAppender,
            TypeAppender<ImplementationDesc> defaultsAppender) {
        this.constructorGenerator = constructorGenerator;
        this.defaultConstructorGenerator = defaultConstructorGenerator;
        this.mutableBuilderAppender = mutableBuilderAppender;
        this.immutableBuilderAppender = immutableBuilderAppender;
        this.defaultsAppender = defaultsAppender;
    }

    @Override
    public void append(TypeSpec.Builder typeSpecBuilder, SpecDesc specDesc, ImplementationDesc valueDesc) {
        if (valueDesc.getConstructionMethod() == ConstructionMethod.CONSTRUCTOR) {
            constructorGenerator.append(typeSpecBuilder, specDesc, valueDesc);
            if (!(valueDesc instanceof BeanDesc) && valueDesc.getProperties().stream().anyMatch(PropertyDesc::isOptional)) {
                defaultConstructorGenerator.append(typeSpecBuilder, specDesc, valueDesc);
            }
        }
        else if (valueDesc.getConstructionMethod() == ConstructionMethod.MUTABLE_BUILDER) {
            mutableBuilderAppender.append(typeSpecBuilder, specDesc, valueDesc);
        }
        else if (valueDesc.getConstructionMethod() == ConstructionMethod.IMMUTABLE_BUILDER) {
            immutableBuilderAppender.append(typeSpecBuilder, specDesc, valueDesc);
        }
        else if (valueDesc.getConstructionMethod() == ConstructionMethod.FROM_DEFAULTS) {
            defaultsAppender.append(typeSpecBuilder, specDesc, valueDesc);
        }
        else {
            throw new IllegalArgumentException("Unsupported construction type");
        }
    }
}
