package com.mattunderscore.specky;

import static com.mattunderscore.specky.ParserUtils.toValue;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.constraint.model.NFDisjointPredicates;
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.model.AbstractTypeDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.mattunderscore.specky.model.generator.scope.Scope;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.proposition.ConstraintFactory;
import com.mattunderscore.specky.proposition.Normaliser;
import com.squareup.javapoet.CodeBlock;

/**
 * Resolver for properties.
 * @author Matt Champion 05/03/2017
 */
/*package*/ final class PropertyResolver {
    private final Normaliser normaliser = new Normaliser();
    private final ConstraintFactory constraintFactory = new ConstraintFactory();
    private final List<PropertyDesc> declaredProperties = new ArrayList<>();
    private final Map<String, AbstractTypeDesc> abstractTypes;
    private final InternalSemanticErrorListener errorListener;

    /**
     * Constructor.
     */
    PropertyResolver(Map<String, AbstractTypeDesc> abstractTypes, InternalSemanticErrorListener errorListener) {
        this.abstractTypes = abstractTypes;
        this.errorListener = errorListener;
    }

    /**
     * Add a declared property.
     */
    void addDeclaredProperty(Specky.PropertyContext context, Scope scope) {
        declaredProperties.add(createProperty(context, scope));
    }

    /**
     * Resolve the declared and inherited properties.
     */
    List<PropertyDesc> resolveProperties(
        Specky.ImplementationSpecContext ctx,
        List<String> supertypes,
        Scope scope) {

        final List<AbstractTypeDesc> resolvedSupertypes = resolveSupertypes(supertypes, scope, ctx);

        final List<PropertyDesc> inheritedProperties = resolvedSupertypes
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
                checkMergableProperties(currentProperty, inheritedProperty, ctx);
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
                final PropertyDesc mergedProperty = mergeDeclaredProperty(currentProperty, property, ctx);
                allProperties.remove(currentProperty);
                allProperties.add(mergedProperty);
                knownProperties.put(propertyName, mergedProperty);
            }
        }
        return allProperties;
    }

    private PropertyDesc mergeDeclaredProperty(PropertyDesc currentProperty, PropertyDesc declaredProperty, ParserRuleContext ctx) {
        checkMergableProperties(currentProperty, declaredProperty, ctx);

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

    private void checkMergableProperties(PropertyDesc currentProperty, PropertyDesc newProperty, ParserRuleContext ctx) {
        if (!newProperty.getType().equals(currentProperty.getType())) {
            errorListener.onSemanticError("Conflicting property declarations for " +
                    currentProperty.getName() +
                    ". Types " +
                    currentProperty.getType() +
                    " and " +
                    newProperty.getType(),
                ctx);
        }
        else if (newProperty.isOptional() != currentProperty.isOptional()) {
            errorListener.onSemanticError("Conflicting property declarations for " +
                    currentProperty.getName() +
                    ". Cannot be both optional and required.",
                ctx);
        }
    }

    private List<AbstractTypeDesc> resolveSupertypes(List<String> supertypes, Scope scope, ParserRuleContext ctx) {
        final List<AbstractTypeDesc> typeDescs = new ArrayList<>();
        resolveSupertypes(supertypes, scope, ctx, typeDescs, new HashSet<>());
        return typeDescs;
    }

    private void resolveSupertypes(
        List<String> supertypes,
        Scope scope,
        ParserRuleContext ctx,
        List<AbstractTypeDesc> typeDescs,
        Set<AbstractTypeDesc> setOfTypes) {

        supertypes
            .stream()
            .map(typeName -> {
                final Optional<String> optionalType = scope.resolveType(typeName);
                return optionalType.orElseGet(() -> {
                    errorListener.onSemanticError("No resolvable type for " + typeName, ctx);
                    return "unknown type";
                });
            })
            .map(abstractTypes::get)
            .forEach(typeDesc -> {
                if (typeDesc == null) {
                    errorListener.onSemanticError("Unknown is not an abstract type", ctx);
                    return;
                }

                if (setOfTypes.add(typeDesc)) {
                    resolveSupertypes(typeDesc.getSupertypes(), scope, ctx, typeDescs, setOfTypes);
                    typeDescs.add(typeDesc);
                }
            });
    }

    private PropertyDesc createProperty(Specky.PropertyContext context, Scope scope) {
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

        final CodeBlock defaultCode = defaultValue != null ? CodeBlock.of(defaultValue) : scope
            .resolveValue(scope.resolveType(context.Identifier().getText()).get(), context.OPTIONAL() != null).get();

        final String resolvedType = scope
            .resolveType(context
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
            .description(toValue(context.STRING_LITERAL()))
            .build();
    }
}
