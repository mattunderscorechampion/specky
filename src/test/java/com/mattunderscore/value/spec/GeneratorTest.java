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

package com.mattunderscore.value.spec;

import com.mattunderscore.value.spec.ValueSpecParser.SpecContext;
import com.mattunderscore.value.spec.model.SpecDesc;
import com.mattunderscore.value.spec.type.resolver.TypeResolver;
import com.mattunderscore.value.spec.type.resolver.TypeResolverBuilder;
import com.squareup.javapoet.JavaFile;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link Generator}.
 * @author Matt Champion on 09/06/16
 */
public class GeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        final CharStream stream = new ANTLRInputStream(SpecBuilderTest
                .class
                .getClassLoader()
                .getResourceAsStream("Test.spec"));
        final com.mattunderscore.value.spec.ValueSpecLexer lexer = new ValueSpecLexer(stream);
        final ValueSpecParser parser = new ValueSpecParser(new UnbufferedTokenStream<CommonToken>(lexer));
        final SpecContext spec = parser.spec();
        final TypeResolver resolver = new TypeResolverBuilder().build(spec);
        final SpecBuilder specBuilder = new SpecBuilder(resolver);

        final SpecDesc specDesc = specBuilder.build(spec);

        final Generator generator = new Generator();
        final List<JavaFile> files = generator.generate(specDesc);
        assertEquals(2, files.size());
        for (JavaFile file : files) {
            file.writeTo(System.out);
        }
    }
}
