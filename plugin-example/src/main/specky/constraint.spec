
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

licence """Copyright © 2016-2017 Matthew Champion
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
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."""

author "Matt Champion"

package com.mattunderscore.specky.constraint.model.test

imports
    com.mattunderscore.specky.constraint.model.test.BinaryConstraintOperator
    com.mattunderscore.specky.constraint.model.test.ConstraintOperator
    com.mattunderscore.specky.constraint.model.test.SubjectModifier default SubjectModifier.IDENTITY

value PredicateDesc "Description of a predicate."
    properties
        String subject "The subject of the constraint."
        SubjectModifier subjectModifier "The modifier to subject of the constraint."
        ConstraintOperator operator "An operator."
        String literal "A literal value."
    options
        immutable builder

value NFDisjointPredicates "Disjunction of predicates."
    properties
        List<PredicateDesc> predicates "The predicates."
    options
        immutable builder

value NFConjoinedDisjointPredicates "Conjunction of disjunctions of predicates."
    properties
        List<NFDisjointPredicates> predicates "The disjunctions of predicates."
    options
        immutable builder

type PropositionalExpression

value Proposition : PropositionalExpression
    properties
        PredicateDesc predicate
    options
        immutable builder

value BinaryPropositionExpression : PropositionalExpression
    properties
        PropositionalExpression expression0
        BinaryConstraintOperator operation
        PropositionalExpression expression1
    options
        immutable builder
