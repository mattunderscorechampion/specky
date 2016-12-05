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

package com.mattunderscore.specky.proposition;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.Variable;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.mattunderscore.specky.constraint.model.BinaryConstraintOperator;
import com.mattunderscore.specky.constraint.model.BinaryPropositionExpression;
import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.constraint.model.NFDisjointPredicates;
import com.mattunderscore.specky.constraint.model.PredicateDesc;
import com.mattunderscore.specky.constraint.model.Proposition;
import com.mattunderscore.specky.constraint.model.PropositionalExpression;

/**
 * Normalise a Propositional expression to conjunctive normal form.
 * @author Matt Champion on 18/10/2016
 */
public final class Normaliser {
    /**
     * Normalise a Propositional expression to conjunctive normal form.
     */
    public NFConjoinedDisjointPredicates normalise(PropositionalExpression expression) {
        final Expression<PredicateDesc> convertedExpression = convertExpression(expression);
        final Expression<PredicateDesc> cnf = RuleSet.toCNF(convertedExpression);
        return convertExpression(cnf);
    }

    private Expression<PredicateDesc> convertExpression(PropositionalExpression expression) {
        if (expression instanceof Proposition) {
            return convertProposition((Proposition) expression);
        }
        else if (expression instanceof BinaryPropositionExpression) {
            return convertBinaryExpression((BinaryPropositionExpression) expression);
        }
        else {
            throw new IllegalArgumentException("The expression " + expression + " cannot be understood");
        }
    }

    private Expression<PredicateDesc> convertProposition(Proposition expression) {
        return Variable.of(expression.getPredicate());
    }

    private Expression<PredicateDesc> convertBinaryExpression(BinaryPropositionExpression expression) {
        final Expression<PredicateDesc> expression0 = convertExpression(expression.getExpression0());
        final Expression<PredicateDesc> expression1 = convertExpression(expression.getExpression1());

        final BinaryConstraintOperator operation = expression.getOperation();
        switch (operation) {
            case CONJUNCTION:
                return And.of(expression0, expression1);
            case DISJUNCTION:
                return Or.of(expression0, expression1);
            default:
                throw new IllegalArgumentException("The expression " + expression + " cannot be understood");
        }
    }

    private NFConjoinedDisjointPredicates convertExpression(Expression<PredicateDesc> expression) {
        final String exprType = expression.getExprType();

        switch (exprType) {
            case And.EXPR_TYPE:
                return convertAndExpression((And<PredicateDesc>)expression);
            case Or.EXPR_TYPE:
                return convertOrExpression((Or<PredicateDesc>)expression);
            case Variable.EXPR_TYPE:
                return convertVariableExpression((Variable<PredicateDesc>)expression);
            default:
                throw new IllegalArgumentException("The expression " + expression + " cannot be understood");
        }
    }

    private NFConjoinedDisjointPredicates convertVariableExpression(Variable<PredicateDesc> expression) {
        return NFConjoinedDisjointPredicates
            .builder()
            .predicates(singletonList(NFDisjointPredicates
                .builder()
                .predicates(singletonList(expression.getValue()))
                .build()))
            .build();
    }

    private NFConjoinedDisjointPredicates convertOrExpression(Or<PredicateDesc> expression) {
        final List<PredicateDesc> predicateDescs = expression
            .getChildren()
            .stream()
            .map(expr -> (Variable<PredicateDesc>) expr)
            .map(Variable::getValue)
            .collect(toList());

        return NFConjoinedDisjointPredicates
            .builder()
            .predicates(singletonList(NFDisjointPredicates
                .builder()
                .predicates(predicateDescs)
                .build()))
            .build();
    }

    private NFConjoinedDisjointPredicates convertAndExpression(And<PredicateDesc> expression) {
        final List<NFDisjointPredicates> disjointPredicates = expression
            .getChildren()
            .stream()
            .map(this::convertExpression)
            .flatMap((predicates) -> predicates.getPredicates().stream())
            .collect(toList());

        return NFConjoinedDisjointPredicates
            .builder()
            .predicates(disjointPredicates)
            .build();
    }
}
