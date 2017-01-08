/* Copyright © 2017 Matthew Champion All rights reserved.

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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.dsl.ConstraintFactory;
import com.mattunderscore.specky.dsl.model.DSLBeanDesc;
import com.mattunderscore.specky.dsl.model.DSLLicence;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;
import com.mattunderscore.specky.proposition.Normaliser;

/**
 * Listener for bean types.
 *
 * @author Matt Champion 06/01/2017
 */
public final class DSLBeanTypeListener extends SpeckyBaseListener {
    private final Normaliser normaliser = new Normaliser();
    private final ConstraintFactory constraintFactory = new ConstraintFactory();

    private final List<DSLBeanDesc> types = new ArrayList<>();
    private Specky.ImplementationSpecContext implementationSpecContext;
    private DSLBeanDesc.Builder currentTypeDesc;

    /**
     * @return the abstract types
     */
    public List<DSLBeanDesc> getBeanTypes() {
        return unmodifiableList(types);
    }

    @Override
    public void enterImplementationSpec(Specky.ImplementationSpecContext ctx) {
        currentTypeDesc = DSLBeanDesc.builder();
        implementationSpecContext = ctx;
    }

    @Override
    public void enterSupertypes(Specky.SupertypesContext ctx) {
        if (!isBean()) {
            return;
        }

        currentTypeDesc = currentTypeDesc.supertypes(ctx
            .Identifier()
            .stream()
            .map(TerminalNode::getText)
            .collect(toList()));
    }

    @Override
    public void enterOpts(Specky.OptsContext ctx) {
        if (!isBean()) {
            return;
        }

        currentTypeDesc = currentTypeDesc
            .constructionMethod(toConstructionDesc(ctx))
            .withModification(withModifications(ctx));
    }

    @Override
    public void exitProps(Specky.PropsContext ctx) {
        if (!isBean()) {
            return;
        }

        currentTypeDesc.properties(
            ctx
                .property()
                .stream()
                .map(this::createProperty)
                .collect(toList()));
    }

    @Override
    public void exitLicence(Specky.LicenceContext ctx) {
        if (!isBean()) {
            return;
        }

        currentTypeDesc.licence(
            DSLLicence
                .builder()
                .ifThen(ctx.Identifier() != null, builder -> builder.identifier(ctx.Identifier().getText()))
                .ifThen(ctx.string_value() != null, builder -> builder.licence(toValue(ctx.string_value())))
                .build());
    }

    @Override
    public void exitImplementationSpec(Specky.ImplementationSpecContext ctx) {
        if (!isBean()) {
            return;
        }

        currentTypeDesc = currentTypeDesc
            .name(ctx.Identifier().getText())
            .ifThen(
                ctx.StringLiteral() == null,
                builder -> builder.description("Bean type $L.\n\nAuto-generated from specification."))
            .ifThen(
                ctx.StringLiteral() != null,
                builder -> builder.description(
                    ctx.StringLiteral().getText().substring(1, ctx.StringLiteral().getText().length() - 1)));

        types.add(currentTypeDesc.build());

        currentTypeDesc = null;
    }

    private DSLPropertyDesc createProperty(Specky.PropertyContext context) {
        final String defaultValue = context.default_value() == null ?
            null :
            context.default_value().ANYTHING().getText();
        final Specky.TypeParametersContext parametersContext = context
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
            .constraint(normaliser
                .normalise(constraintFactory
                    .create(context.propertyName().getText(), context.constraint_statement())))
            .description(context.StringLiteral() == null ?
                null :
                context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
            .build();
    }

    private boolean isBean() {
        return implementationSpecContext != null && implementationSpecContext.BEAN() != null;
    }

    private static String toValue(Specky.String_valueContext stringValue) {
        if (stringValue == null) {
            return null;
        }

        final TerminalNode multiline = stringValue.MULTILINE_STRING_LITERAL();
        if (multiline != null) {
            final String literal = multiline.getText();
            return literal.substring(3, literal.length() - 3);
        }
        else {
            final String literal = stringValue.StringLiteral().getText();
            return literal.substring(1, literal.length() - 1);
        }
    }

    private static ConstructionMethod toConstructionDesc(Specky.OptsContext options) {
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
        else if ("from defaults".equals(token)) {
            return ConstructionMethod.FROM_DEFAULTS;
        }
        else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    private static boolean withModifications(Specky.OptsContext options) {
        return !(options == null || options.WITH_MODIFICATION() == null);
    }
}
