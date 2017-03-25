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

import com.mattunderscore.specky.literal.model.ComplexLiteral;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.literal.model.RealLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
import com.squareup.javapoet.ClassName;
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
        else if (literalDesc instanceof ComplexLiteral) {
            final CodeBlock.Builder builder = CodeBlock
                    .builder()
                    .add("new $T(", ClassName.bestGuess(((ComplexLiteral) literalDesc).getTypeName()));
            ((ComplexLiteral) literalDesc)
                    .getSubvalues()
                    .stream()
                    .map(this::generate)
                    .forEach(builder::add);
            builder.add(")");
            return builder.build();
        }
        else {
            throw new IllegalArgumentException(literalDesc + " not supported");
        }
    }
}
