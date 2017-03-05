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

import static com.mattunderscore.specky.ParserUtils.toConstructionDesc;
import static com.mattunderscore.specky.ParserUtils.toValue;
import static com.mattunderscore.specky.ParserUtils.withModifications;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.model.generator.scope.EvaluateTemplate;
import com.mattunderscore.specky.model.generator.scope.Scope;
import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;

import net.jcip.annotations.NotThreadSafe;

/**
 * Listener for value types.
 * @author Matt Champion 05/01/2017
 */
@NotThreadSafe
public final class ValueListener extends SpeckyBaseListener {
    private final SectionScopeResolver sectionScopeResolver;
    private final Map<String, AbstractTypeDesc> abstractTypes;
    private final InternalSemanticErrorListener semanticErrorListener;
    private final List<ValueDesc> valueDescs = new ArrayList<>();

    private Specky.ImplementationSpecContext implementationSpecContext;
    private ValueDesc.Builder currentTypeDesc = ValueDesc.builder();
    private String currentSection;
    private List<String> currentSupertypes;
    private PropertyResolver propertyResolver;

    /**
     * Constructor.
     */
    public ValueListener(
            SectionScopeResolver sectionScopeResolver,
            Map<String, AbstractTypeDesc> abstractTypes,
            InternalSemanticErrorListener semanticErrorListener) {

        this.sectionScopeResolver = sectionScopeResolver;
        this.abstractTypes = abstractTypes;
        this.semanticErrorListener = semanticErrorListener;
    }

    /**
     * @return the value types
     */
    public List<ValueDesc> getValueDescs() {
        return unmodifiableList(valueDescs);
    }

    @Override
    public void enterImplementationSpec(Specky.ImplementationSpecContext ctx) {
        currentTypeDesc = ValueDesc.builder();
        currentSupertypes = emptyList();
        propertyResolver = new PropertyResolver(abstractTypes, semanticErrorListener);
        implementationSpecContext = ctx;
    }

    @Override
    public void enterSupertypes(Specky.SupertypesContext ctx) {
        final List<String> supertypes = ctx
            .Identifier()
            .stream()
            .map(TerminalNode::getText)
            .collect(toList());
        currentSupertypes = supertypes;
        currentTypeDesc = currentTypeDesc.supertypes(supertypes);
    }

    @Override
    public void exitProps(Specky.PropsContext ctx) {
        if (!isValue()) {
            return;
        }

        ctx
            .property()
            .forEach(propertyContext ->
                propertyResolver.addDeclaredProperty(propertyContext, sectionScopeResolver.resolve(currentSection)));
    }

    @Override
    public void enterSectionDeclaration(Specky.SectionDeclarationContext ctx) {
        currentSection = toValue(ctx.string_value());
    }

    @Override
    public void enterOpts(Specky.OptsContext ctx) {
        if (!isValue()) {
            return;
        }

        currentTypeDesc = currentTypeDesc
            .constructionMethod(toConstructionDesc(ctx))
            .withModification(withModifications(ctx));
    }

    @Override
    public void exitLicence(Specky.LicenceContext ctx) {
        if (!isValue()) {
            return;
        }

        currentTypeDesc.licence(null);
    }

    @Override
    public void exitImplementationSpec(Specky.ImplementationSpecContext ctx) {
        if (!isValue()) {
            currentTypeDesc = ValueDesc.builder();
            return;
        }

        final Scope scope = sectionScopeResolver.resolve(currentSection);
        final EvaluateTemplate formatter = new EvaluateTemplate(scope.toTemplateContext(ctx.Identifier().getText()));

        final List<PropertyDesc> allProperties = propertyResolver.resolveProperties(ctx, currentSupertypes, scope);

        currentTypeDesc = currentTypeDesc
            .name(ctx.Identifier().getText())
            .author(scope.getAuthor())
            .packageName(scope.getPackage())
            .ifThen(
                ctx.licence() == null,
                builder -> builder
                    .licence(formatter.apply(
                        scope
                            .resolveLicence((String) null)
                            .orElse(null))))
            .ifThen(
                ctx.licence() != null && ctx.licence().string_value() != null,
                builder -> builder.licence(formatter.apply(toValue(ctx.licence().string_value()))))
            .ifThen(
                ctx.licence() != null && ctx.licence().Identifier() != null,
                builder -> {
                    final String licenceName = ctx.licence().Identifier().getText();
                    return builder
                        .licence(formatter.apply(
                            scope
                                .resolveLicence(licenceName)
                                .orElseGet(() -> {
                                    semanticErrorListener.onSemanticError(
                                        "An unknown name was used to reference a licence",
                                        ctx.licence());
                                    return null;
                                })));
                })
            .ifThen(
                ctx.StringLiteral() == null,
                builder -> builder.description(formatter.apply("Value type ${type}.\n\nAuto-generated from specification.")))
            .ifThen(
                ctx.StringLiteral() != null,
                builder -> builder.description(formatter.apply(toValue(ctx.StringLiteral()))))
            .properties(allProperties);

        valueDescs.add(currentTypeDesc.build());
    }

    private boolean isValue() {
        return implementationSpecContext != null && implementationSpecContext.VALUE() != null;
    }
}
