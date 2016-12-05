package com.mattunderscore.specky.proposition;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mattunderscore.specky.constraint.model.BinaryConstraintOperator;
import com.mattunderscore.specky.constraint.model.BinaryPropositionExpression;
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

    @Test
    public void normaliseConjunction() {
        final Normaliser normaliser = new Normaliser();

        final PredicateDesc predicate0 = PredicateDesc
            .builder()
            .subject("a")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("b")
            .build();

        final PredicateDesc predicate1 = PredicateDesc
            .builder()
            .subject("c")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("d")
            .build();

        final NFConjoinedDisjointPredicates cnf = normaliser
            .normalise(
                BinaryPropositionExpression
                    .builder()
                    .operation(BinaryConstraintOperator.CONJUNCTION)
                    .expression0(Proposition.builder().predicate(predicate0).build())
                    .expression1(Proposition.builder().predicate(predicate1).build())
                    .build());

        assertEquals(NFConjoinedDisjointPredicates
            .builder()
            .predicates(asList(
                NFDisjointPredicates
                    .builder()
                    .predicates(singletonList(predicate0))
                    .build(),
                NFDisjointPredicates
                    .builder()
                    .predicates(singletonList(predicate1))
                    .build()))
            .build(), cnf);
    }

    @Test
    public void normaliseDisjunction() {
        final Normaliser normaliser = new Normaliser();

        final PredicateDesc predicate0 = PredicateDesc
            .builder()
            .subject("a")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("b")
            .build();

        final PredicateDesc predicate1 = PredicateDesc
            .builder()
            .subject("c")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("d")
            .build();

        final NFConjoinedDisjointPredicates cnf = normaliser
            .normalise(
                BinaryPropositionExpression
                    .builder()
                    .operation(BinaryConstraintOperator.DISJUNCTION)
                    .expression0(Proposition.builder().predicate(predicate0).build())
                    .expression1(Proposition.builder().predicate(predicate1).build())
                    .build());

        assertEquals(NFConjoinedDisjointPredicates
            .builder()
            .predicates(singletonList(NFDisjointPredicates
                .builder()
                .predicates(asList(predicate0, predicate1))
                .build()))
            .build(), cnf);
    }

    @Test
    public void normaliseComplex() {
        final Normaliser normaliser = new Normaliser();

        final PredicateDesc predicate0 = PredicateDesc
            .builder()
            .subject("a")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("b")
            .build();

        final PredicateDesc predicate1 = PredicateDesc
            .builder()
            .subject("c")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("d")
            .build();

        final PredicateDesc predicate2 = PredicateDesc
            .builder()
            .subject("e")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("f")
            .build();

        final PredicateDesc predicate3 = PredicateDesc
            .builder()
            .subject("g")
            .operator(ConstraintOperator.EQUAL_TO)
            .literal("h")
            .build();

        final NFConjoinedDisjointPredicates cnf = normaliser
            .normalise(
                BinaryPropositionExpression
                    .builder()
                    .operation(BinaryConstraintOperator.DISJUNCTION)
                    .expression0(BinaryPropositionExpression
                        .builder()
                        .operation(BinaryConstraintOperator.CONJUNCTION)
                        .expression0(Proposition.builder().predicate(predicate0).build())
                        .expression1(Proposition.builder().predicate(predicate1).build())
                        .build())
                    .expression1(BinaryPropositionExpression
                        .builder()
                        .operation(BinaryConstraintOperator.DISJUNCTION)
                        .expression0(Proposition.builder().predicate(predicate2).build())
                        .expression1(Proposition.builder().predicate(predicate3).build())
                        .build())
                    .build());

        assertEquals(
            NFConjoinedDisjointPredicates
                .builder()
                .predicates(asList(
                    NFDisjointPredicates
                        .builder()
                        .predicates(asList(predicate0, predicate2, predicate3))
                        .build(),
                    NFDisjointPredicates
                        .builder()
                        .predicates(asList(predicate1, predicate2, predicate3))
                        .build()))
                .build(),
            cnf);
    }
}
