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

package com.mattunderscore.specky.proposition;

import com.mattunderscore.specky.constraint.model.ConstraintOperator;
import com.mattunderscore.specky.constraint.model.PredicateDesc;
import com.mattunderscore.specky.constraint.model.SubjectModifier;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.Constraint_propositionContext;

/**
 * Factory for predicates.
 *
 * @author Matt Champion 28/01/2017
 */
/*package*/ final class PredicateFactory {
    PredicateDesc createPredicate(String propertyName, Specky.Constraint_predicateContext predicate) {
        return PredicateDesc
            .builder()
            .subject(propertyName)
            .operator(toConstraintOperator(predicate.constraint_operator()))
            .literal(predicate.constraint_literal().getText())
            .build();
    }

    PredicateDesc modifyPredicate(PredicateDesc predicateToModify, Constraint_propositionContext expression) {
        if (expression.NEGATION() != null) {
            return PredicateDesc
                .builder()
                .subject(predicateToModify.getSubject())
                .subjectModifier(predicateToModify.getSubjectModifier())
                .operator(negateOperator(predicateToModify.getOperator()))
                .literal(predicateToModify.getLiteral())
                .build();
        }
        else if (expression.HAS_SOME() != null) {
            return PredicateDesc
                .builder()
                .subject(predicateToModify.getSubject())
                .subjectModifier(SubjectModifier.HAS_SOME)
                .operator(predicateToModify.getOperator())
                .literal(predicateToModify.getLiteral())
                .build();
        }
        else if (expression.SIZE_OF() != null) {
            return PredicateDesc
                .builder()
                .subject(predicateToModify.getSubject())
                .subjectModifier(SubjectModifier.SIZE_OF)
                .operator(predicateToModify.getOperator())
                .literal(predicateToModify.getLiteral())
                .build();
        }
        else {
            throw new IllegalArgumentException("Unknown how to deal with " + expression);
        }
    }

    private static ConstraintOperator toConstraintOperator(Specky.Constraint_operatorContext operatorContext) {
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

    private static ConstraintOperator negateOperator(ConstraintOperator operator) {
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
}
