/* Copyright © 2016-2017 Matthew Champion All rights reserved.

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

import static com.mattunderscore.specky.constraint.model.BinaryConstraintOperator.CONJUNCTION;
import static com.mattunderscore.specky.constraint.model.BinaryConstraintOperator.DISJUNCTION;
import static java.util.stream.Collectors.toList;

import java.util.Iterator;
import java.util.List;

import com.mattunderscore.specky.constraint.model.BinaryConstraintOperator;
import com.mattunderscore.specky.constraint.model.BinaryPropositionExpression;
import com.mattunderscore.specky.constraint.model.Proposition;
import com.mattunderscore.specky.constraint.model.PropositionalExpression;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.Constraint_statementContext;

/**
 * Factory for constraints.
 *
 * @author Matt Champion on 05/12/2016
 */
public final class ConstraintFactory {
    private final PropositionFactory propositionFactory = new PropositionFactory();

    /**
     * @return a constraint
     */
    public PropositionalExpression create(String propertyName, Constraint_statementContext statementContext) {
        if (statementContext == null) {
            return null;
        }

        return createConstraint(propertyName, statementContext.constraint_expression());
    }

    private PropositionalExpression createConstraint(String propertyName, Specky.Constraint_expressionContext expression) {
        final List<Specky.Constraint_propositionContext> propositionContexts = expression.constraint_proposition();
        if (propositionContexts.size() == 1) {
            return propositionFactory.createProposition(propertyName, propositionContexts.get(0));
        }
        else if (!expression.CONJUNCTION().isEmpty()) {
            final List<Proposition> propositions = propositionContexts
                .stream()
                .map(expr -> propositionFactory.createProposition(propertyName, expr))
                .collect(toList());

            return joinPropositions(CONJUNCTION, propositions);
        }
        else if (!expression.DISJUNCTION().isEmpty()) {
            final List<Proposition> propositions = propositionContexts
                .stream()
                .map(expr -> propositionFactory.createProposition(propertyName, expr))
                .collect(toList());

            return joinPropositions(DISJUNCTION, propositions);
        }
        else {
            return createConstraint(propertyName, expression.constraint_expression());
        }
    }

    private PropositionalExpression joinPropositions(BinaryConstraintOperator operation, Iterable<Proposition> propositions) {
        final Iterator<Proposition> expressionIterator = propositions.iterator();

        PropositionalExpression prop = expressionIterator.next();
        do {
            prop = BinaryPropositionExpression
                .builder()
                .operation(operation)
                .expression0(prop)
                .expression1(expressionIterator.next())
                .build();
        }
        while (expressionIterator.hasNext());

        return prop;
    }
}
