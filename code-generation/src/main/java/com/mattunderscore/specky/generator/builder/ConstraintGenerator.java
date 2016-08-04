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

package com.mattunderscore.specky.generator.builder;

import com.mattunderscore.specky.constraint.model.ConstraintDesc;
import com.mattunderscore.specky.constraint.model.UnaryConstraintDesc;
import com.mattunderscore.specky.model.PropertyDesc;
import com.squareup.javapoet.CodeBlock;

/**
 * Constraint generator.
 * @author Matt Champion on 01/08/2016
 */
public final class ConstraintGenerator {
    /**
     * Generate constraint checking code.
     */
    public CodeBlock generate(PropertyDesc propertyDesc) {
        final CodeBlock.Builder builder = CodeBlock.builder();

        generate(builder, propertyDesc.getName(), propertyDesc.getConstraint());
        return builder.build();
    }

    private void generate(CodeBlock.Builder builder, String propertyName, ConstraintDesc constraintDesc) {
        final UnaryConstraintDesc unaryConstraint = constraintDesc.getUnaryConstraint();
        if (unaryConstraint != null) {
            generate(builder, propertyName, unaryConstraint);
        }
        else {
            throw new UnsupportedOperationException("Code generator for binary expressions not yet supported");
        }
    }

    private void generate(CodeBlock.Builder builder, String propertyName, UnaryConstraintDesc unaryConstraintDesc) {
        final int bound = Integer.parseInt(unaryConstraintDesc.getLiteral());
        switch (unaryConstraintDesc.getOperator()) {
            case GREATER_THAN:
                builder.beginControlFlow("if ($L <= $L)", propertyName, bound);
                break;
            case LESS_THAN:
                builder.beginControlFlow("if ($L >= $L)", propertyName, bound);
                break;
            case GREATER_THAN_OR_EQUAL:
                builder.beginControlFlow("if ($L < $L)", propertyName, bound);
                break;
            case LESS_THAN_OR_EQUAL:
                builder.beginControlFlow("if ($L > $L)", propertyName, bound);
                break;
            default:
                throw new IllegalArgumentException("Constraint not valid");
        }

        builder
            .addStatement("throw new IllegalArgumentException(\"Constraint violated\")")
            .endControlFlow();
    }
}
