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

import static com.mattunderscore.specky.constraint.model.BinaryConstraintOperator.CONJUNCTION;
import static com.mattunderscore.specky.constraint.model.BinaryConstraintOperator.DISJUNCTION;
import static java.util.stream.Collectors.toList;

import java.util.Iterator;
import java.util.List;

import com.mattunderscore.specky.constraint.model.BinaryPropositionExpression;
import com.mattunderscore.specky.constraint.model.ConstraintOperator;
import com.mattunderscore.specky.constraint.model.PredicateDesc;
import com.mattunderscore.specky.constraint.model.Proposition;
import com.mattunderscore.specky.constraint.model.PropositionalExpression;
import com.mattunderscore.specky.constraint.model.SubjectModifier;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.Constraint_statementContext;

/**
 * Factory for constraints.
 *
 * @author Matt Champion on 05/12/2016
 */
public final class ConstraintFactory {
    /**
     * @return a constraint
     */
    public PropositionalExpression create(String propertyName, Constraint_statementContext statementContext) {
        if (statementContext == null) {
            return null;
        }

        final Specky.Constraint_expressionContext expression =
            statementContext.constraint_expression();
        return createConstraint(propertyName, expression);
    }

    private PropositionalExpression createConstraint(String propertyName, Specky.Constraint_expressionContext expression) {
        final List<Specky.Constraint_propositionContext> propositionContexts = expression.constraint_proposition();
        if (propositionContexts.size() == 1) {
            return createConstraint(propertyName, propositionContexts.get(0));
        }
        else if (!expression.CONJUNCTION().isEmpty()) {
            final Iterator<Proposition> expressionIterator = propositionContexts
                .stream()
                .map(expr -> createConstraint(propertyName, expr))
                .collect(toList())
                .iterator();

            PropositionalExpression prop = expressionIterator.next();

            do {
                prop = BinaryPropositionExpression
                    .builder()
                    .operation(CONJUNCTION)
                    .expression0(prop)
                    .expression1(expressionIterator.next())
                    .build();
            }
            while (expressionIterator.hasNext());

            return prop;
        }
        else if (!expression.DISJUNCTION().isEmpty()) {
            final Iterator<Proposition> expressionIterator = propositionContexts
                .stream()
                .map(expr -> createConstraint(propertyName, expr))
                .collect(toList())
                .iterator();

            PropositionalExpression prop = expressionIterator.next();

            do {
                prop = BinaryPropositionExpression
                    .builder()
                    .operation(DISJUNCTION)
                    .expression0(prop)
                    .expression1(expressionIterator.next())
                    .build();
            }
            while (expressionIterator.hasNext());

            return prop;
        }
        else {
            return createConstraint(propertyName, expression.constraint_expression());
        }
    }

    private Proposition createConstraint(String propertyName, Specky.Constraint_propositionContext expression) {
        final Specky.Constraint_predicateContext predicate = expression.constraint_predicate();
        final Specky.Constraint_propositionContext subexpression = expression.constraint_proposition();

        assert predicate != null || subexpression != null : "Should either be predicate or another expression";

        if (predicate != null) {
            return Proposition
                .builder()
                .predicate(
                    PredicateDesc
                        .builder()
                        .subject(propertyName)
                        .operator(toConstraintOperator(predicate.constraint_operator()))
                        .literal(predicate.constraint_literal().getText())
                        .build())
                .build();
        }
        else if (expression.NEGATION() != null) {
            final Proposition propositionToNegate = createConstraint(propertyName, subexpression);
            final PredicateDesc predicateToNegate = propositionToNegate.getPredicate();
            return Proposition
                .builder()
                .predicate(
                    PredicateDesc
                        .builder()
                        .subject(predicateToNegate.getSubject())
                        .subjectModifier(predicateToNegate.getSubjectModifier())
                        .operator(negateOperator(predicateToNegate.getOperator()))
                        .literal(predicateToNegate.getLiteral())
                        .build())
                .build();
        }
        else {
            final Proposition propositionOfSubject = createConstraint(propertyName, subexpression);
            final PredicateDesc predicateOfSubject = propositionOfSubject.getPredicate();
            return Proposition
                .builder()
                .predicate(
                    PredicateDesc
                        .builder()
                        .subject(propertyName)
                        .subjectModifier(expression.HAS_SOME() != null ? SubjectModifier.HAS_SOME : SubjectModifier.SIZE_OF)
                        .operator(predicateOfSubject.getOperator())
                        .literal(predicateOfSubject.getLiteral())
                        .build())
                .build();
        }
    }

    private ConstraintOperator negateOperator(ConstraintOperator operator) {
        switch (operator) {
            case LESS_THAN_OR_EQUAL:
                return ConstraintOperator.GREATER_THAN;
            case GREATER_THAN_OR_EQUAL:
                return ConstraintOperator.LESS_THAN;
            case LESS_THAN:
                return ConstraintOperator.GREATER_THAN_OR_EQUAL;
            case GREATER_THAN:
                return ConstraintOperator.LESS_THAN_OR_EQUAL;
            case EQUAL_TO:
                return ConstraintOperator.NOT_EQUAL_TO;
            default:
                throw new IllegalArgumentException("Unsupported operator");
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
        else if ("=".equals(operatorContextText)) {
            return ConstraintOperator.EQUAL_TO;
        }
        else {
            throw new IllegalArgumentException("Unsupported operator");
        }
    }
}
