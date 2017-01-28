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

import com.mattunderscore.specky.constraint.model.PredicateDesc;
import com.mattunderscore.specky.constraint.model.Proposition;
import com.mattunderscore.specky.parser.Specky;

/**
 * Factory for creating propositions from predicates.
 *
 * @author Matt Champion 28/01/2017
 */
/*package*/ final class PropositionFactory {
    private final PredicateFactory predicateFactory = new PredicateFactory();

    Proposition createProposition(String propertyName, Specky.Constraint_propositionContext expression) {
        final Specky.Constraint_predicateContext predicate = expression.constraint_predicate();
        final Specky.Constraint_propositionContext subexpression = expression.constraint_proposition();

        assert predicate != null || subexpression != null : "Should either be predicate or another expression";

        if (predicate != null) {
            return createProposition(predicateFactory.createPredicate(propertyName, predicate));
        }
        else {
            final Proposition propositionOfSubexpression = createProposition(propertyName, subexpression);
            final PredicateDesc predicateOfSubexpression = propositionOfSubexpression.getPredicate();
            return createProposition(predicateFactory.modifyPredicate(predicateOfSubexpression, expression));
        }
    }

    private Proposition createProposition(PredicateDesc predicateDesc) {
        return Proposition
            .builder()
            .predicate(predicateDesc)
            .build();
    }
}
