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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.dsl.model.DSLBeanDesc;
import com.mattunderscore.specky.dsl.model.DSLConstructionMethod;
import com.mattunderscore.specky.dsl.model.DSLImportDesc;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.ImplementationSpecContext;
import com.mattunderscore.specky.parser.Specky.ImportsContext;
import com.mattunderscore.specky.parser.Specky.PropertyContext;
import com.mattunderscore.specky.parser.Specky.SpecContext;
import com.mattunderscore.specky.parser.Specky.TypeParametersContext;
import com.mattunderscore.specky.parser.Specky.TypeSpecContext;

/**
 * Processor for the ANTLR4 generated AST. Returns a better representation of the DSL.
 *
 * @author Matt Champion on 05/06/16
 */
public final class SpecBuilder {

    /**
     * Constructor.
     */
    public SpecBuilder() {
    }

    /**
     * @return a {@link DSLSpecDesc} from a {@link SpecContext}.
     */
    public DSLSpecDesc build(SpecContext context) {
        final ImportsContext importsContext = context.imports();
        final List<DSLImportDesc> imports = importsContext == null ?
            emptyList() :
            importsContext
                .singleImport()
                .stream()
                .map(singleImportContext -> DSLImportDesc
                    .builder()
                    .typeName(singleImportContext.qualifiedName().getText())
                    .defaultValue(singleImportContext.default_value() == null ?
                        null :
                        singleImportContext.default_value().ANYTHING().getText())
                    .build())
                .collect(toList());
        return DSLSpecDesc
            .builder()
            .author(context.author() == null ?
                null :
                context
                    .author()
                    .StringLiteral()
                    .getText()
                    .substring(1, context.author().StringLiteral().getText().length() - 1))
            .packageName(context.package_name().qualifiedName().getText())
            .importTypes(imports)
            .views(context
                .typeSpec()
                .stream()
                .map(this::createView)
                .collect(toList()))
            .types(context
                .implementationSpec()
                .stream()
                .map(this::createType)
                .collect(toList()))
            .build();
    }

    private DSLTypeDesc createType(ImplementationSpecContext context) {
        final String typeName = context.Identifier().get(0).getText();
        final List<DSLPropertyDesc> properties = context.props() == null ?
            emptyList() :
            context
                .props()
                .property()
                .stream()
                .map(this::createProperty)
                .collect(toList());
        final DSLConstructionMethod constructionMethod = toConstructionDesc(context);
        final List<String> supertypes;
        if (context.Identifier().size() > 1) {
            supertypes = context
                .Identifier()
                .subList(1, context.Identifier().size())
                .stream()
                .map(TerminalNode::getText)
                .collect(toList());
        }
        else {
            supertypes = emptyList();
        }
        if (context.BEAN() == null) {
            return DSLValueDesc
                .builder()
                .name(typeName)
                .properties(properties)
                .constructionMethod(constructionMethod)
                .supertypes(supertypes)
                .description(context.StringLiteral() == null ?
                    "Value type $L.\n\nAuto-generated from specification." :
                    context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
                .build();
        }
        else {
            return DSLBeanDesc
                .builder()
                .name(typeName)
                .properties(properties)
                .constructionMethod(constructionMethod)
                .supertypes(supertypes)
                .description(context.StringLiteral() == null ?
                    "Bean type $L.\n\nAuto-generated from specification." :
                    context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
                .build();
        }
    }

    private DSLViewDesc createView(TypeSpecContext context) {
        final String typeName = context.Identifier().getText();
        final List<DSLPropertyDesc> properties = context.props() == null ?
            emptyList() :
            context
                .props()
                .property()
                .stream()
                .map(this::createProperty)
                .collect(toList());
        return DSLViewDesc
                .builder()
                .name(typeName)
                .properties(properties)
                .description(context.StringLiteral() == null ?
                    "View type $L.\n\nAuto-generated from specification." :
                    context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
                .build();
        }

    private DSLPropertyDesc createProperty(PropertyContext context) {
        final String defaultValue = context.default_value() == null ?
            null :
            context.default_value().ANYTHING().getText();
        final TypeParametersContext parametersContext = context
                .typeParameters();
        final List<String> typeParameters = parametersContext == null ?
            emptyList() :
            parametersContext
                .Identifier()
                .stream()
                .map(ParseTree::getText)
                .collect(toList());
        return DSLPropertyDesc
            .builder()
            .name(context
                .propertyName()
                .getText())
            .type(context
                .Identifier()
                .getText())
            .typeParameters(typeParameters)
            .optional(context.OPTIONAL() != null)
            .defaultValue(defaultValue)
            .description(context.StringLiteral() == null ?
                null :
                context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
            .build();
    }

    private DSLConstructionMethod toConstructionDesc(ImplementationSpecContext typeSpec) {
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
