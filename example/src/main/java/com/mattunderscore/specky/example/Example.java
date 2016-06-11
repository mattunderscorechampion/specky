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

package com.mattunderscore.specky.example;

import com.mattunderscore.specky.Writer;
import com.mattunderscore.specky.parser.ValueSpecLexer;
import com.mattunderscore.specky.parser.ValueSpecParser;
import com.mattunderscore.specky.parser.ValueSpecParser.SpecContext;
import com.mattunderscore.specky.generator.Generator;
import com.mattunderscore.specky.SpecBuilder;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.squareup.javapoet.JavaFile;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;

import java.io.IOException;
import java.util.List;

/**
 * @author Matt Champion on 10/06/16
 */
public final class Example {
    public static void main(String[] args) throws IOException {
        final CharStream stream = new ANTLRInputStream(Example
            .class
            .getClassLoader()
            .getResourceAsStream("Example.spec"));
        final ValueSpecLexer lexer = new ValueSpecLexer(stream);
        final ValueSpecParser parser = new ValueSpecParser(new UnbufferedTokenStream<CommonToken>(lexer));
        final SpecContext spec = parser.spec();
        final TypeResolver resolver = new TypeResolverBuilder().build(spec);
        final SpecBuilder specBuilder = new SpecBuilder(resolver);

        final SpecDesc specDesc = specBuilder.build(spec);

        final Generator generator = new Generator();
        final List<JavaFile> files = generator.generate(specDesc);

        final Writer writer = new Writer("target/example-output");
        writer.write(files);
    }
}
