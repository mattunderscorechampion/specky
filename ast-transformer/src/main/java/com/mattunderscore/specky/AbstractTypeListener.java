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

import static com.mattunderscore.specky.ParserUtils.toValue;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;
import com.mattunderscore.specky.proposition.Normaliser;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.squareup.javapoet.CodeBlock;

import net.jcip.annotations.NotThreadSafe;

/**
 * Listener for abstract types.
 * @author Matt Champion 05/01/2017
 */
@NotThreadSafe
public final class AbstractTypeListener extends SpeckyBaseListener {
    private final Normaliser normaliser = new Normaliser();
    private final ConstraintFactory constraintFactory = new ConstraintFactory();
    private final SectionScopeResolver sectionScopeResolver;
    private final SemanticErrorListener semanticErrorListener;

    private final List<AbstractTypeDesc> abstractTypeDescs = new ArrayList<>();
    private AbstractTypeDesc.Builder currentTypeDesc;
    private String currentSection;

    /**
     * Constructor.
     */
    public AbstractTypeListener(SectionScopeResolver sectionScopeResolver, SemanticErrorListener semanticErrorListener) {
        this.sectionScopeResolver = sectionScopeResolver;
        this.semanticErrorListener = semanticErrorListener;
    }

    /**
     * @return the abstract types
     */
    public List<AbstractTypeDesc> getAbstractTypeDescs() {
        return unmodifiableList(abstractTypeDescs);
    }

    @Override
    public void enterTypeSpec(Specky.TypeSpecContext ctx) {
        currentTypeDesc = AbstractTypeDesc.builder();
    }

    @Override
    public void enterSupertypes(Specky.SupertypesContext ctx) {
        if (!isAbstractType()) {
            return;
        }

        currentTypeDesc = currentTypeDesc.supertypes(ctx
            .Identifier()
            .stream()
            .map(TerminalNode::getText)
            .collect(toList()));
    }

    @Override
    public void exitProps(Specky.PropsContext ctx) {
        if (!isAbstractType()) {
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
    public void enterSectionDeclaration(Specky.SectionDeclarationContext ctx) {
        currentSection = toValue(ctx.string_value());
    }

    @Override
    public void exitLicence(Specky.LicenceContext ctx) {
        currentTypeDesc.licence(null);
    }

    @Override
    public void exitTypeSpec(Specky.TypeSpecContext ctx) {
        currentTypeDesc = currentTypeDesc
            .name(ctx.Identifier().getText())
            .author(sectionScopeResolver.resolve(currentSection).getAuthor())
            .packageName(sectionScopeResolver.resolve(currentSection).getPackage())
            .ifThen(
                ctx.licence() == null,
                builder -> builder
                    .licence(sectionScopeResolver
                        .resolve(currentSection)
                        .getLicenceResolver()
                        .resolve((String) null)
                        .orElse(null)))
            .ifThen(
                ctx.licence() != null && ctx.licence().string_value() != null,
                builder -> builder.licence(toValue(ctx.licence().string_value())))
            .ifThen(
                ctx.licence() != null && ctx.licence().Identifier() != null,
                builder -> {
                    final String licenceName = ctx.licence().Identifier().getText();
                    return builder
                        .licence(sectionScopeResolver
                            .resolve(currentSection)
                            .getLicenceResolver()
                            .resolve(licenceName)
                            .orElseGet(() -> {
                                semanticErrorListener.onSemanticError(
                                    "An unknown name was used to reference a licence",
                                    ctx.licence());
                                return null;
                            }));
                })
            .ifThen(
                ctx.StringLiteral() == null,
                builder -> builder.description("Abstract type $L.\n\nAuto-generated from specification."))
            .ifThen(
                ctx.StringLiteral() != null,
                builder -> builder.description(toValue(ctx.StringLiteral())));

        abstractTypeDescs.add(currentTypeDesc.build());
        currentTypeDesc = null;
    }

    private boolean isAbstractType() {
        return currentTypeDesc != null;
    }

    private PropertyDesc createProperty(Specky.PropertyContext context) {
        final TypeResolver typeResolver = sectionScopeResolver
            .resolve(currentSection)
            .getTypeResolver();

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

        final CodeBlock defaultCode = defaultValue != null ? CodeBlock.of(defaultValue) : sectionScopeResolver
            .resolve(currentSection)
            .getValueResolver()
            .resolve(typeResolver.resolve(context.Identifier().getText()).get(), context.OPTIONAL() != null)
            .get();

        final String resolvedType = typeResolver
            .resolve(context
                .Identifier()
                .getText(),
            context.OPTIONAL() != null)
            .get();

        return PropertyDesc
            .builder()
            .name(context
                .propertyName()
                .getText())
            .type(resolvedType)
            .typeParameters(typeParameters)
            .optional(context.OPTIONAL() != null)
            .defaultValue(defaultCode)
            .constraint(normaliser
                .normalise(constraintFactory
                    .create(context.propertyName().getText(), context.constraint_statement())))
            .description(toValue(context.StringLiteral()))
            .build();
    }
}
