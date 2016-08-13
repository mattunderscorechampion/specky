
licence """Copyright © 2016 Matthew Champion
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

value PredicateDesc "Description of a constraint."
    properties
        SubjectModifier subject "The subject of the constraint."
        ConstraintOperator operator "An operator."
        String literal "A literal value."
    options
        immutable builder

value CompoundConstraintDesc "Description of a constraint."
    properties
        ConstraintDesc constraint0
        BinaryConstraintOperator operator "An operator."
        ConstraintDesc constraint1
    options
        immutable builder

value ConstraintDesc "Description of a constraint."
    properties
        optional CompoundConstraintDesc binaryConstraint
        optional PredicateDesc unaryConstraint
        optional ConstraintDesc negatedConstraint
    options
        immutable builder

value NFDisjointPredicates "Disjunction of predicates."
    properties
        List<PredicateDesc> predicates "The predicates."
    options
        immutable builder

value NFConjoinedDisjointPredicates "Conujunction of disjunctions of predicates."
    properties
        List<NFDisjointPredicates> predicates "The disjunctions of predicates."
    options
        immutable builder
