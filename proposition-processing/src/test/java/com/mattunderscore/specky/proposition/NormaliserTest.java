package com.mattunderscore.specky.proposition;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mattunderscore.specky.constraint.model.ConstraintOperator;
import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.constraint.model.NFDisjointPredicates;
import com.mattunderscore.specky.constraint.model.PredicateDesc;
import com.mattunderscore.specky.constraint.model.Proposition;

/**
 * Unit tests for {@link Normaliser}.
 *
 * @author Matt Champion on 18/10/2016
 */
public final class NormaliserTest {

    @Test
    public void normaliseProposition() {
        final Normaliser normaliser = new Normaliser();

        final PredicateDesc predicate = PredicateDesc
            .builder()
            .subject("a")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("b")
            .build();

        final NFConjoinedDisjointPredicates cnf = normaliser.normalise(Proposition
            .builder()
            .predicate(predicate)
            .build());

        assertEquals(NFConjoinedDisjointPredicates
            .builder()
            .predicates(singletonList(NFDisjointPredicates
                .builder()
                .predicates(singletonList(predicate))
                .build()))
            .build(), cnf);
    }
}
