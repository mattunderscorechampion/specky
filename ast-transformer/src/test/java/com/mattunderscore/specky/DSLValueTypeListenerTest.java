/* Copyright © 2017 Matthew Champion All rights reserved.

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

package com.mattunderscore.specky;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import com.mattunderscore.specky.constraint.model.NFConjoinedDisjointPredicates;
import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;
import com.mattunderscore.specky.dsl.model.DSLValueDesc;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;

/**
 * Unit tests for {@link DSLValueTypeListener}.
 *
 * @author Matt Champion 05/01/2017
 */
public final class DSLValueTypeListenerTest {

    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(SectionImportTypeListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("SectionTest.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final DSLValueTypeListener listener = new DSLValueTypeListener();
        parser.addParseListener(listener);

        parser.spec();

        final List<DSLValueDesc> types = listener.getValueTypes();

        assertEquals(1, types.size());
        assertEquals(
            DSLValueDesc
                .builder()
                .name("FirstValue")
                .description("Value type $L.\n\nAuto-generated from specification.")
                .constructionMethod(ConstructionMethod.CONSTRUCTOR)
                .properties(asList(
                    DSLPropertyDesc
                        .builder()
                        .name("num")
                        .type("Integer")
                        .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
                        .build(),
                    DSLPropertyDesc
                        .builder()
                        .name("str")
                        .type("String")
                        .constraint(NFConjoinedDisjointPredicates.builder().predicates(emptyList()).build())
                        .build()))
                .build(),
            types.get(0));
    }
}