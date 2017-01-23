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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.constraint.model.NFDisjointPredicates;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.ValueDesc;
import com.mattunderscore.specky.model.generator.scope.Scope;
import com.mattunderscore.specky.model.generator.scope.SectionScopeResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;
import com.mattunderscore.specky.proposition.Normaliser;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.squareup.javapoet.CodeBlock;

import net.jcip.annotations.NotThreadSafe;

/**
 * Listener for value types.
 * @author Matt Champion 05/01/2017
 */
@NotThreadSafe
public final class ValueListener extends SpeckyBaseListener {
    private final Normaliser normaliser = new Normaliser();
    private final ConstraintFactory constraintFactory = new ConstraintFactory();
    private final SectionScopeResolver sectionScopeResolver;
    private final Map<String, AbstractTypeDesc> abstractTypes;
    private final SemanticErrorListener semanticErrorListener;
    private final List<ValueDesc> valueDescs = new ArrayList<>();

    private Specky.ImplementationSpecContext implementationSpecContext;
    private ValueDesc.Builder currentTypeDesc = ValueDesc.builder();
    private String currentSection;
    private List<String> currentSupertypes;
    private List<PropertyDesc> declaredProperties;

    /**
     * Constructor.
     */
    public ValueListener(
            SectionScopeResolver sectionScopeResolver,
            Map<String, AbstractTypeDesc> abstractTypes,
            SemanticErrorListener semanticErrorListener) {

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
        declaredProperties = emptyList();
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

        declaredProperties = ctx
            .property()
            .stream()
            .map(this::createProperty)
            .collect(toList());
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
        currentTypeDesc.licence(null);
    }

    @Override
    public void exitImplementationSpec(Specky.ImplementationSpecContext ctx) {
        if (!isValue()) {
            currentTypeDesc = ValueDesc.builder();
            return;
        }

        final List<AbstractTypeDesc> resolveSupertypes =
            resolveSupertypes(currentSupertypes, sectionScopeResolver.resolve(currentSection));

        final List<PropertyDesc> inheritedProperties = resolveSupertypes
            .stream()
            .map(AbstractTypeDesc::getProperties)
            .flatMap(Collection::stream)
            .collect(toList());

        final Map<String, PropertyDesc> knownProperties = new HashMap<>();
        final List<PropertyDesc> allProperties = new ArrayList<>();
        for (final PropertyDesc property : inheritedProperties) {
            final PropertyDesc inheritedProperty = PropertyDesc
                .builder()
                .name(property.getName())
                .description(property.getDescription())
                .type(property.getType())
                .typeParameters(property.getTypeParameters())
                .defaultValue(property.getDefaultValue())
                .constraint(property.getConstraint())
                .optional(property.isOptional())
                .override(true)
                .build();
            final String propertyName = property.getName();
            final PropertyDesc currentProperty = knownProperties.get(propertyName);
            if (currentProperty == null) {
                allProperties.add(inheritedProperty);
                knownProperties.put(propertyName, inheritedProperty);
            }
            else {
                checkMergableProperties(currentProperty, inheritedProperty);
            }
        }
        for (final PropertyDesc property : declaredProperties) {
            final String propertyName = property.getName();
            final PropertyDesc currentProperty = knownProperties.get(propertyName);
            if (currentProperty == null) {
                allProperties.add(property);
                knownProperties.put(propertyName, property);
            }
            else {
                final PropertyDesc mergedProperty = mergeDeclaredProperty(currentProperty, property);
                allProperties.remove(currentProperty);
                allProperties.add(mergedProperty);
                knownProperties.put(propertyName, mergedProperty);
            }
        }

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
                builder -> builder
                    .licence(sectionScopeResolver
                        .resolve(currentSection)
                        .getLicenceResolver()
                        .resolve(ctx.licence().Identifier().getText())
                        .get()))
            .ifThen(
                ctx.StringLiteral() == null,
                builder -> builder.description("Value type $L.\n\nAuto-generated from specification."))
            .ifThen(
                ctx.StringLiteral() != null,
                builder -> builder.description(
                    ctx.StringLiteral().getText().substring(1, ctx.StringLiteral().getText().length() - 1)))
            .properties(allProperties);

        valueDescs.add(currentTypeDesc.build());
    }

    private List<AbstractTypeDesc> resolveSupertypes(List<String> supertypes, Scope scope) {
        final List<AbstractTypeDesc> typeDescs = new ArrayList<>();
        resolveSupertypes(supertypes, scope, typeDescs, new HashSet<>());
        return typeDescs;
    }

    private void resolveSupertypes(
        List<String> supertypes,
        Scope scope,
        List<AbstractTypeDesc> typeDescs,
        Set<AbstractTypeDesc> setOfTypes) {

        supertypes
            .stream()
            .map(typeName -> {
                final Optional<String> optionalType = scope.getTypeResolver().resolve(typeName);
                return optionalType.orElseGet(() -> {
                    semanticErrorListener.onSemanticError("No resolvable type for " + typeName);
                    return "unknown type";
                });
            })
            .map(abstractTypes::get)
            .forEach(typeDesc -> {
                if (typeDesc == null) {
                    semanticErrorListener.onSemanticError("Unknown is not an abstract type");
                    return;
                }

                if (setOfTypes.add(typeDesc)) {
                    resolveSupertypes(typeDesc.getSupertypes(), scope, typeDescs, setOfTypes);
                    typeDescs.add(typeDesc);
                }
            });
    }

    private boolean isValue() {
        return implementationSpecContext != null && implementationSpecContext.VALUE() != null;
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
            .resolve(typeResolver.resolve(context.Identifier().getText()).get(), context.OPTIONAL() != null).get();

        final String resolvedType = sectionScopeResolver
            .resolve(currentSection)
            .getPropertyTypeResolver()
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
            .description(context.StringLiteral() == null ?
                null :
                context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
            .build();
    }

    private PropertyDesc mergeDeclaredProperty(PropertyDesc currentProperty, PropertyDesc declaredProperty) {
        checkMergableProperties(currentProperty, declaredProperty);

        final List<NFDisjointPredicates> constraints = new ArrayList<>();

        if (currentProperty.getConstraint() != null) {
            constraints.addAll(currentProperty.getConstraint().getPredicates());
        }
        if (declaredProperty.getConstraint() != null) {
            constraints.addAll(declaredProperty.getConstraint().getPredicates());
        }

        return PropertyDesc
            .builder()
            .name(declaredProperty.getName())
            .description(declaredProperty.getDescription())
            .type(declaredProperty.getType())
            .typeParameters(declaredProperty.getTypeParameters())
            .optional(declaredProperty.isOptional())
            .defaultValue(declaredProperty.getDefaultValue())
            .override(true)
            .constraint(NFConjoinedDisjointPredicates
                .builder()
                .predicates(constraints)
                .build())
            .build();
    }

    private void checkMergableProperties(PropertyDesc currentProperty, PropertyDesc newProperty) {
        if (!newProperty.getType().equals(currentProperty.getType())) {
            semanticErrorListener.onSemanticError("Conflicting property declarations for " +
                currentProperty.getName() +
                ". Types " +
                currentProperty.getType() +
                " and " +
                newProperty.getType());
        }
        else if (newProperty.isOptional() != currentProperty.isOptional()) {
            semanticErrorListener.onSemanticError("Conflicting property declarations for " +
                currentProperty.getName() +
                ". Cannot be both optional and required.");
        }
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
