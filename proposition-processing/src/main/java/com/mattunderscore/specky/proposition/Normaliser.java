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

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.constraint.model.NFDisjointPredicates;
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
        if (expression instanceof Proposition) {
            return normaliseProposition((Proposition)expression);
        }
        throw new UnsupportedOperationException("CNF normalisation not yet implemented");
    }

    private NFConjoinedDisjointPredicates normaliseProposition(Proposition expression) {
        return NFConjoinedDisjointPredicates
            .builder()
            .predicates(singletonList(NFDisjointPredicates
                .builder()
                .predicates(singletonList(expression.getPredicate()))
                .build()))
            .build();
    }
}