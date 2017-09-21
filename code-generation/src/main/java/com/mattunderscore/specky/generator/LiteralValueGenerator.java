/* Copyright © 2017 Matthew Champion
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

package com.mattunderscore.specky.generator;

import static com.mattunderscore.specky.model.ConstructionMethod.FROM_DEFAULTS;
import static com.mattunderscore.specky.model.ConstructionMethod.IMMUTABLE_BUILDER;
import static com.mattunderscore.specky.model.ConstructionMethod.MUTABLE_BUILDER;
import static com.squareup.javapoet.ClassName.bestGuess;
import static java.lang.Character.toUpperCase;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.mattunderscore.specky.literal.model.ComplexLiteral;
import com.mattunderscore.specky.literal.model.ConstantLiteral;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.literal.model.NamedComplexLiteral;
import com.mattunderscore.specky.literal.model.RealLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.squareup.javapoet.CodeBlock;

/**
 * Generator for literal values.
 * @author Matt Champion on 25/03/17
 */
public final class LiteralValueGenerator {
    /**
     * Generate code for a literal description.
     */
    public CodeBlock generate(LiteralDesc literalDesc) {
        if (literalDesc == null) {
            return null;
        }
        else if (literalDesc instanceof IntegerLiteral) {
            return CodeBlock.of("$L", ((IntegerLiteral) literalDesc).getIntegerLiteral());
        }
        else if (literalDesc instanceof RealLiteral) {
            return CodeBlock.of("$L", ((RealLiteral) literalDesc).getRealLiteral());
        }
        else if (literalDesc instanceof StringLiteral) {
            return CodeBlock.of("$S", ((StringLiteral) literalDesc).getStringLiteral());
        }
        else if (literalDesc instanceof UnstructuredLiteral) {
            return CodeBlock.of(((UnstructuredLiteral) literalDesc).getLiteral());
        }
        else if (literalDesc instanceof ConstantLiteral) {
            final ConstantLiteral constantLiteral = (ConstantLiteral) literalDesc;
            return CodeBlock.of("$T.$N", bestGuess(constantLiteral.getTypeName()), constantLiteral.getConstant());
        }
        else {
            return generateComplexLiteral(literalDesc);
        }
    }

    private CodeBlock generateComplexLiteral(LiteralDesc literalDesc) {
        if (literalDesc instanceof ComplexLiteral) {
            final ComplexLiteral complexLiteral = (ComplexLiteral) literalDesc;
            return useConstructor(complexLiteral.getTypeName(), complexLiteral.getSubvalues());
        }
        else if (literalDesc instanceof NamedComplexLiteral) {
            final NamedComplexLiteral complexLiteral = (NamedComplexLiteral) literalDesc;
            final String typeName = complexLiteral.getTypeName();
            final List<LiteralDesc> subvalues = complexLiteral.getSubvalues();
            final ConstructionMethod constructionMethod = complexLiteral.getConstructionMethod();
            if (constructionMethod == IMMUTABLE_BUILDER || constructionMethod == MUTABLE_BUILDER) {
                final List<String> names = complexLiteral.getNames();
                return useBuilder(typeName, names, subvalues);
            }
            else if (constructionMethod == FROM_DEFAULTS) {
                final List<String> names = complexLiteral.getNames();
                return useDefaults(typeName, names, subvalues);
            }
            else {
                // Assume order
                return useConstructor(typeName, subvalues);
            }
        }
        else {
            throw new IllegalArgumentException(literalDesc + " not supported");
        }
    }

    private CodeBlock useDefaults(String typeName, List<String> names, List<LiteralDesc> subvalues) {
        final CodeBlock.Builder builder = CodeBlock
            .builder()
            .add("$T.defaults()", bestGuess(typeName));

        for (int i = 0; i < names.size(); i++) {
            builder.add(".$N(", "with" + toUpperCase(names.get(i).charAt(0)) + names.get(i).substring(1));
            builder.add(generate(subvalues.get(i)));
            builder.add(")");
        }

        return builder.build();
    }

    private CodeBlock useBuilder(String typeName, List<String> names, List<LiteralDesc> subvalues) {
        final CodeBlock.Builder builder = CodeBlock
                .builder()
                .add("$T.builder()", bestGuess(typeName));
        for (int i = 0; i < names.size(); i++) {
            builder.add(".$N(", names.get(i));
            builder.add(generate(subvalues.get(i)));
            builder.add(")");
        }
        builder.add(".build()");
        return builder.build();
    }

    private CodeBlock useConstructor(String typeName, List<LiteralDesc> subvalues) {
        final CodeBlock.Builder builder = CodeBlock
                .builder()
                .add("new $T(", bestGuess(typeName));
        builder.add(subvalues
                .stream()
                .map(this::generate)
                .map(Objects::toString)
                .collect(Collectors.joining(", ")));
        builder.add(")");
        return builder.build();
    }
}
