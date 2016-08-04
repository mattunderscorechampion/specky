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

import com.mattunderscore.specky.constraint.model.BinaryConstraintOperator;
import com.mattunderscore.specky.constraint.model.CompoundConstraintDesc;
import com.mattunderscore.specky.constraint.model.ConstraintDesc;
import com.mattunderscore.specky.constraint.model.ConstraintOperator;
import com.mattunderscore.specky.constraint.model.PredicateDesc;
import com.mattunderscore.specky.dsl.model.DSLBeanDesc;
import com.mattunderscore.specky.dsl.model.DSLImportDesc;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.dsl.model.DSLTypeDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.dsl.model.DSLViewDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
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
        final ConstructionMethod constructionMethod = toConstructionDesc(context);
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
            .constraint(createConstraint(context.constraint_statement()))
            .description(context.StringLiteral() == null ?
                null :
                context.StringLiteral().getText().substring(1, context.StringLiteral().getText().length() - 1))
            .build();
    }

    private ConstraintDesc createConstraint(Specky.Constraint_statementContext statementContext) {
        if (statementContext == null) {
            return null;
        }

        final Specky.Constraint_compound_expressionContext expression = statementContext.constraint_compound_expression();
        return createConstraint(expression);
    }

    private ConstraintDesc createConstraint(Specky.Constraint_compound_expressionContext expression) {
        final List<Specky.Constraint_expressionContext> subexpressions = expression.constraint_expression();
        if (subexpressions.size() == 1) {
            return createConstraint(subexpressions.get(0));
        }
        else {
            final Specky.Constraint_expressionContext leftConstraint = expression.constraint_expression(0);
            final Specky.Constraint_expressionContext rightConstraint = expression.constraint_expression(1);
            final BinaryConstraintOperator operator = toConstraintOperator(expression.constraint_compound_operator());

            return ConstraintDesc
                .builder()
                .binaryConstraint(CompoundConstraintDesc
                    .builder()
                    .constraint0(createConstraint(leftConstraint))
                    .constraint1(createConstraint(rightConstraint))
                    .operator(operator)
                    .build())
                .build();
        }
    }

    private ConstraintDesc createConstraint(Specky.Constraint_expressionContext expression) {
        final Specky.Constraint_predicateContext unaryExpression = expression.constraint_predicate();
        if (unaryExpression != null) {
            return ConstraintDesc
                .builder()
                .unaryConstraint(PredicateDesc
                    .builder()
                    .operator(toConstraintOperator(unaryExpression.constraint_operator()))
                    .literal(unaryExpression.constraint_literal().getText())
                    .build())
                .build();
        }
        else if (expression.NEGATION() != null) {
            return ConstraintDesc
                .builder()
                .negatedConstraint(createConstraint(expression.constraint_expression()))
                .build();
        }
        else {
            return createConstraint(expression.constraint_expression());
        }
    }


    private ConstraintOperator toConstraintOperator(Specky.Constraint_operatorContext operatorContext) {
        final String operatorContextText = operatorContext.getText();
        if ("<=".equals(operatorContextText)) {
            return ConstraintOperator.LESS_THAN_OR_EQUAL;
        }
        else if (">=".equals(operatorContextText)) {
            return ConstraintOperator.GREATER_THAN_OR_EQUAL;
        }
        else if ("<".equals(operatorContextText)) {
            return ConstraintOperator.LESS_THAN;
        }
        else if (">".equals(operatorContextText)) {
            return ConstraintOperator.GREATER_THAN;
        }
        else {
            throw new IllegalArgumentException("Unsupported operator");
        }
    }

    private BinaryConstraintOperator toConstraintOperator(Specky.Constraint_compound_operatorContext operatorContext) {
        final String operatorContextText = operatorContext.getText();
        if ("&".equals(operatorContextText)) {
            return BinaryConstraintOperator.CONJUNCTION;
        }
        else if ("|".equals(operatorContextText)) {
            return BinaryConstraintOperator.DISJUNCTION;
        }
        else {
            throw new IllegalArgumentException("Unsupported operator");
        }
    }

    private ConstructionMethod toConstructionDesc(ImplementationSpecContext typeSpec) {
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
