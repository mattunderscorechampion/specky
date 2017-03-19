
section "Constraints"

note """Constraints are used to apply runtime restrictions on the allowed values of properties. A property constraint
can only be applied to a single property. It is intended that you will able to apply constraints to multiple properties
but this is not yet implemented. A property constraint is checked when setting a mutable property, in the constructor
of types built with the constructor or in the configurator method of the builder for methods built that way. A violated
constraint will result in a IllegalArgumentException being thrown.

A constraint is a propositional formula with & used to indicate a conjunction | used to indicate a disjunction and ! a
negation. The predicates are used to test a property. Each predicate is a boolean expression. Each predicate expression
of a property constraint has an optional subject modifier, an operator and literal value. The subject of the predicate
is implicitly the property. The subject modifier is currently only used by collection types to allow the predicate to
be applied to its contents. The operator identifies the type of comparison to be applied. The modified subject is one
operand of the operator. The literal value is the other operand.

A constraint's satisfiability is not checked."""

author "Matt Champion"

package com.mattunderscore.specky.constraint.model

imports
    com.mattunderscore.specky.constraint.model.BinaryConstraintOperator
    com.mattunderscore.specky.constraint.model.ConstraintOperator
    com.mattunderscore.specky.constraint.model.SubjectModifier default SubjectModifier.IDENTITY

value PredicateDesc "Description of a predicate."
    properties
        String subject "The subject of the constraint."
        SubjectModifier subjectModifier "The modifier to subject of the constraint."
        ConstraintOperator operator "An operator."
        String literal "A literal value."
    licence BSD3Clause
    options
        immutable builder

value NFDisjointPredicates "Disjunction of predicates."
    properties
        List<PredicateDesc> predicates "The predicates."
    licence BSD3Clause
    options
        immutable builder

value NFConjoinedDisjointPredicates "Conjunction of disjunctions of predicates."
    properties
        List<NFDisjointPredicates> predicates "The disjunctions of predicates."
    licence BSD3Clause
    options
        immutable builder

type PropositionalExpression
    licence BSD3Clause

value Proposition : PropositionalExpression
    properties
        PredicateDesc predicate
    licence BSD3Clause
    options
        immutable builder

value BinaryPropositionExpression : PropositionalExpression
    properties
        PropositionalExpression expression0
        BinaryConstraintOperator operation
        PropositionalExpression expression1
    licence BSD3Clause
    options
        immutable builder
