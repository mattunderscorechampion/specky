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

package com.mattunderscore.specky.dsl;

import static java.util.stream.Collectors.toList;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.model.BeanDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.PropertyImplementationDesc;
import com.mattunderscore.specky.model.PropertyViewDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.TypeDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.model.ViewDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.ImplSpecContext;
import com.mattunderscore.specky.parser.Specky.PropertyContext;
import com.mattunderscore.specky.parser.Specky.PropertyViewContext;
import com.mattunderscore.specky.parser.Specky.SpecContext;
import com.mattunderscore.specky.parser.Specky.TypeSpecContext;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;

/**
 * @author Matt Champion on 05/06/16
 */
public final class SpecBuilder {
    private final TypeResolver resolver;
    private final DefaultValueResolver valueResolver;

    public SpecBuilder(TypeResolver resolver, DefaultValueResolver valueResolver) {
        this.resolver = resolver;
        this.valueResolver = valueResolver;
    }

    public SpecDesc build(SpecContext context) {
        return SpecDesc
            .builder()
            .packageName(context.r_package().qualifiedName().getText())
            .views(context
                .typeSpec()
                .stream()
                .map(this::createValue)
                .collect(toList()))
            .values(context
                .implSpec()
                .stream()
                .map(this::createValue)
                .collect(toList()))
            .build();
    }

    private TypeDesc createValue(ImplSpecContext context) {
        if (context.BEAN() == null) {
            final ValueDesc.ValueDescBuilder valueDescBuilder = ValueDesc
                .builder()
                .name(context.Identifier().get(0).getText())
                .properties(context
                    .property()
                    .stream()
                    .map(this::createProperty)
                    .collect(toList()))
                .constructionMethod(toConstructionDesc(context));

            if (context.Identifier().size() > 1) {
                valueDescBuilder
                    .extend(context
                        .Identifier()
                        .subList(1, context.Identifier().size())
                        .stream()
                        .map(TerminalNode::getText)
                        .collect(toList()));
            }

            return valueDescBuilder.build();
        }
        else {
            return BeanDesc
                .builder()
                .name(context.Identifier().get(0).getText())
                .properties(context
                    .property()
                    .stream()
                    .map(this::createProperty)
                    .collect(toList()))
                .constructionMethod(toConstructionDesc(context))
                .build();
        }
    }

    private ViewDesc createValue(TypeSpecContext context) {
            return ViewDesc
                .builder()
                .name(context.Identifier().getText())
                .properties(context
                    .propertyView()
                    .stream()
                    .map(this::createPropertyView)
                    .collect(toList()))
                .build();
        }

    private PropertyViewDesc createPropertyView(PropertyViewContext propertyViewContext) {
        return PropertyViewDesc
            .builder()
            .name(propertyViewContext.Identifier().get(1).getText())
            .type(resolver.resolve(propertyViewContext.Identifier().get(0).getText()).get())
            .build();
    }

    private PropertyImplementationDesc createProperty(PropertyContext context) {
        final String type = resolver
            .resolve(context
                .Identifier()
                .get(0)
                .getText())
            .get();
        final String defaultValue = context.r_default() == null ?
            valueResolver.resolve(type).get() :
            context.r_default().ANYTHING().getText();
        return PropertyImplementationDesc
            .builder()
            .name(context
                .Identifier()
                .get(1)
                .getText())
            .type(type)
            .optional(context.OPTIONAL() != null)
            .defaultValue(defaultValue)
            .build();
    }

    private ConstructionMethod toConstructionDesc(ImplSpecContext typeSpec) {
        final Specky.OptsContext options = typeSpec.opts();

        if (options == null || options.construction() == null) {
            return ConstructionMethod.CONSTRUCTOR;
        }

        final String token = options.construction().getText();
        if ("constructor".equals(token)) {
            return ConstructionMethod.CONSTRUCTOR;
        }
        else if ("builder".equals(token)) {
            return ConstructionMethod.MUTABLE_BUILDER;
        }
        else if ("immutable builder".equals(token)) {
            return ConstructionMethod.IMMUTABLE_BUILDER;
        }
        else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }
}
