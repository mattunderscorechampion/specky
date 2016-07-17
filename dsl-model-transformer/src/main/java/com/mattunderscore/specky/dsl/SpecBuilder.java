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

import com.mattunderscore.specky.dsl.model.DSLBeanDesc;
import com.mattunderscore.specky.dsl.model.DSLBeanDesc.DSLBeanDescBuilder;
import com.mattunderscore.specky.dsl.model.DSLConstructionMethod;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc.DSLValueDescBuilder;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.ImplmentationSpecContext;
import com.mattunderscore.specky.parser.Specky.PropertyContext;
import com.mattunderscore.specky.parser.Specky.SpecContext;
import com.mattunderscore.specky.parser.Specky.TypeParametersContext;
import com.mattunderscore.specky.parser.Specky.TypeSpecContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Matt Champion on 05/06/16
 */
public final class SpecBuilder {

    public SpecBuilder() {
    }

    public DSLSpecDesc build(SpecContext context) {
        return DSLSpecDesc
            .builder()
            .packageName(context.package_name().qualifiedName().getText())
            .views(context
                .typeSpec()
                .stream()
                .map(this::createView)
                .collect(toList()))
            .values(context
                .implmentationSpec()
                .stream()
                .map(this::createType)
                .collect(toList()))
            .build();
    }

    private DSLTypeDesc createType(ImplmentationSpecContext context) {
        if (context.BEAN() == null) {
            final DSLValueDescBuilder valueDescBuilder = DSLValueDesc
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
                    .supertypes(context
                        .Identifier()
                        .subList(1, context.Identifier().size())
                        .stream()
                        .map(TerminalNode::getText)
                        .collect(toList()));
            }
            else {
                valueDescBuilder.supertypes(Collections.emptyList());
            }

            return valueDescBuilder.build();
        }
        else {
            final DSLBeanDescBuilder beanDescBuilder = DSLBeanDesc
                .builder()
                .name(context.Identifier().get(0).getText())
                .properties(context
                    .property()
                    .stream()
                    .map(this::createProperty)
                    .collect(toList()))
                .constructionMethod(toConstructionDesc(context));

            if (context.Identifier().size() > 1) {
                beanDescBuilder
                    .supertypes(context
                        .Identifier()
                        .subList(1, context.Identifier().size())
                        .stream()
                        .map(TerminalNode::getText)
                        .collect(toList()));
            }
            else {
                beanDescBuilder.supertypes(Collections.emptyList());
            }

            return beanDescBuilder
                .build();
        }
    }

    private DSLViewDesc createView(TypeSpecContext context) {
            return DSLViewDesc
                .builder()
                .name(context.Identifier().getText())
                .properties(context
                    .property()
                    .stream()
                    .map(this::createProperty)
                    .collect(toList()))
                .build();
        }

    private DSLPropertyDesc createProperty(PropertyContext context) {
        final String defaultValue = context.default_value() == null ?
            null :
            context.default_value().ANYTHING().getText();
        final TypeParametersContext parametersContext = context
                .typeParameters();
        final List<String> typeParameters = parametersContext == null ?
            Collections.emptyList() :
            parametersContext
                .Identifier()
                .stream()
                .map(ParseTree::getText)
                .collect(toList());
        return DSLPropertyDesc
            .builder()
            .name(context
                .Identifier()
                .get(1)
                .getText())
            .type(context
                .Identifier()
                .get(0)
                .getText())
            .typeParameters(typeParameters)
            .optional(context.OPTIONAL() != null)
            .defaultValue(defaultValue)
            .build();
    }

    private DSLConstructionMethod toConstructionDesc(ImplmentationSpecContext typeSpec) {
        final Specky.OptsContext options = typeSpec.opts();

        if (options == null || options.construction() == null) {
            return DSLConstructionMethod.CONSTRUCTOR;
        }

        final String token = options.construction().getText();
        if ("constructor".equals(token)) {
            return DSLConstructionMethod.CONSTRUCTOR;
        }
        else if ("builder".equals(token)) {
            return DSLConstructionMethod.MUTABLE_BUILDER;
        }
        else if ("immutable builder".equals(token)) {
            return DSLConstructionMethod.IMMUTABLE_BUILDER;
        }
        else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }
}
