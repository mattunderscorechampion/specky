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

package com.mattunderscore.specky;

import static java.util.stream.Collectors.toList;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ConstructionDesc;
import com.mattunderscore.specky.model.PropertySpec;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.parser.ValueSpecParser.PropertyContext;
import com.mattunderscore.specky.parser.ValueSpecParser.SpecContext;
import com.mattunderscore.specky.parser.ValueSpecParser.TypeSpecContext;
import com.mattunderscore.specky.parser.ValueSpecParser.ConstructionContext;
import com.mattunderscore.specky.type.resolver.TypeResolver;

/**
 * @author Matt Champion on 05/06/16
 */
public final class SpecBuilder {
    private final TypeResolver resolver;

    public SpecBuilder(TypeResolver resolver) {
        this.resolver = resolver;
    }

    public SpecDesc build(SpecContext context) {
        return SpecDesc
            .builder()
            .packageName(context.r_package().qualifiedName().getText())
            .values(context
                .typeSpec()
                .stream()
                .map(this::createValue)
                .collect(toList()))
            .build();
    }

    private TypeDesc createValue(TypeSpecContext context) {
        if (context.BEAN() == null) {
            return ValueDesc
                .builder()
                .name(context.TypeName().getText())
                .properties(context
                    .property()
                    .stream()
                    .map(this::createProperty)
                    .collect(toList()))
                .construction(toConstructionDesc(context))
                .build();
        }
        else {
            return BeanDesc
                .builder()
                .name(context.TypeName().getText())
                .properties(context
                    .property()
                    .stream()
                    .map(this::createProperty)
                    .collect(toList()))
                .build();
        }
    }

    private PropertySpec createProperty(PropertyContext context) {
        return PropertySpec
            .builder()
            .name(context
                .PropertyName()
                .getText())
            .type(resolver
                .resolve(context
                    .TypeName()
                    .getText())
                .get())
            .optional(context.OPTIONAL() != null)
            .defaultValue(null)
            .build();
    }

    private ConstructionDesc toConstructionDesc(TypeSpecContext typeSpec) {
        final ConstructionContext construction = typeSpec.construction();

        if (construction == null) {
            return ConstructionDesc.CONSTRUCTOR;
        }

        final String token = construction.getText();
        if ("constructor".equals(token)) {
            return ConstructionDesc.CONSTRUCTOR;
        }
        else if ("builder".equals(token)) {
            return ConstructionDesc.MUTABLE_BUILDER;
        }
        else if ("immutable builder".equals(token)) {
            return ConstructionDesc.IMMUTABLE_BUILDER;
        }
        else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }
}
