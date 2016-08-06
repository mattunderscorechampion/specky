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

package com.mattunderscore.specky.generator.constraint;

import static java.util.Arrays.asList;

import java.util.List;

import com.mattunderscore.specky.constraint.model.PredicateDesc;
import com.mattunderscore.specky.constraint.model.SubjectModifier;
import com.mattunderscore.specky.model.PropertyDesc;

/**
 * Implementation of {@link PropertyPredicateViolationGenerator}.
 *
 * @author Matt Champion on 06/08/2016
 */
public final class PropertyPredicateViolationGeneratorImpl implements PropertyPredicateViolationGenerator {
    private static final List<String> COLLECTION_TYPES = asList("java.util.Set", "java.util.List");
    private final PropertyPredicateViolationGenerator simpleGenerator = new VerySimplePredicateViolationGenerator();
    private final PropertyPredicateViolationGenerator boxedGenerator = new BoxedPredicateViolationGenerator();
    private final PropertyPredicateViolationGenerator longSimpleGenerator =
        new LongWrapperPropertyPredicateViolationGenerator(new VerySimplePredicateViolationGenerator());
    private final PropertyPredicateViolationGenerator longBoxedGenerator =
        new LongWrapperPropertyPredicateViolationGenerator(new BoxedPredicateViolationGenerator());
    private final PropertyPredicateViolationGenerator bigIntegerGenerator =
        new BigIntegerWrapperPropertyPredicateViolationGenerator(new BoxedPredicateViolationGenerator());
    private final PropertyPredicateViolationGenerator bigDecimalGenerator =
        new BigDecimalWrapperPropertyPredicateViolationGenerator(new BoxedPredicateViolationGenerator());
    private final PropertyPredicateViolationGenerator equalsGenerator = new EqualsPredicateViolationGenerator();

    @Override
    public String generate(PropertyDesc propertyDesc, PredicateDesc predicateDesc) {
        final String type = propertyDesc.getType();

        validateSubjectModifier(predicateDesc, type);

        if (COLLECTION_TYPES.contains(type)) {
            return generateCollectionConstraint(propertyDesc, predicateDesc);
        }
        else {
            return generateValueConstraint(propertyDesc, predicateDesc);
        }
    }

    private String generateValueConstraint(PropertyDesc propertyDesc, PredicateDesc predicateDesc) {
        final String type = propertyDesc.getType();
        switch (type) {
            case "int":
            case "double":
                return simpleGenerator.generate(propertyDesc, predicateDesc);
            case "java.lang.Integer":
            case "java.lang.Double":
                return boxedGenerator.generate(propertyDesc, predicateDesc);
            case "long":
                return longSimpleGenerator.generate(propertyDesc, predicateDesc);
            case "java.lang.Long":
                return longBoxedGenerator.generate(propertyDesc, predicateDesc);
            case "java.math.BigInteger":
                return bigIntegerGenerator.generate(propertyDesc, predicateDesc);
            case "java.math.BigDecimal":
                return bigDecimalGenerator.generate(propertyDesc, predicateDesc);
            case "java.lang.String":
                return equalsGenerator.generate(propertyDesc, predicateDesc);
            default:
                throw new IllegalArgumentException("Constraints not supported for type " + type);
        }
    }

    private String generateCollectionConstraint(PropertyDesc propertyDesc, PredicateDesc predicateDesc) {
        if (predicateDesc.getSubject() == SubjectModifier.SIZE_OF) {
            final PropertyDesc modifiedSubject  = PropertyDesc
                .builder()
                .name(propertyDesc.getName() + ".size()")
                .type("int")
                .optional(propertyDesc.isOptional())
                .build();
            final PredicateDesc modifiedPredicate = PredicateDesc
                .builder()
                .operator(predicateDesc.getOperator())
                .literal(predicateDesc.getLiteral())
                .build();
            return generate(modifiedSubject, modifiedPredicate);
        }
        else {
            throw new IllegalArgumentException("Existential qualification not yet supported");
        }
    }

    private void validateSubjectModifier(PredicateDesc predicateDesc, String type) {
        if (predicateDesc.getSubject() == SubjectModifier.IDENTITY && COLLECTION_TYPES.contains(type)) {

            throw new IllegalArgumentException("A subject modifier is required for working on collections");
        }
        else if (predicateDesc.getSubject() != SubjectModifier.IDENTITY && !COLLECTION_TYPES.contains(type)) {

            throw new IllegalArgumentException("A subject modifier is only allowed for working on collections, not " + type);
        }
    }
}
