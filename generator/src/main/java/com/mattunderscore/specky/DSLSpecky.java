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

package com.mattunderscore.specky;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;

import com.mattunderscore.specky.dsl.SpecBuilder;
import com.mattunderscore.specky.generator.AccessorGenerator;
import com.mattunderscore.specky.generator.BeanGenerator;
import com.mattunderscore.specky.generator.BuildMethodGenerator;
import com.mattunderscore.specky.generator.ConstructorGenerator;
import com.mattunderscore.specky.generator.Generator;
import com.mattunderscore.specky.generator.ImmutableBuilderGenerator;
import com.mattunderscore.specky.generator.MutableBuilderGenerator;
import com.mattunderscore.specky.generator.ValueGenerator;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.output.Writer;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.squareup.javapoet.JavaFile;

/**
 * @author Matt Champion on 18/06/2016
 */
public final class DSLSpecky {
    private final Generator generator;

    public DSLSpecky() {
        final BuildMethodGenerator buildMethodGenerator = new BuildMethodGenerator();
        final MutableBuilderGenerator mutableBuilderGenerator = new MutableBuilderGenerator(buildMethodGenerator);
        final ImmutableBuilderGenerator immutableBuilderGenerator = new ImmutableBuilderGenerator(buildMethodGenerator);
        final AccessorGenerator accessorGenerator = new AccessorGenerator();
        generator = new Generator(
            new ValueGenerator(
                mutableBuilderGenerator,
                immutableBuilderGenerator,
                new ConstructorGenerator(), accessorGenerator),
            new BeanGenerator(mutableBuilderGenerator, immutableBuilderGenerator, accessorGenerator));
    }

    public List<JavaFile> generate(InputStream inputStream) throws IOException {
        final CharStream stream = new ANTLRInputStream(inputStream);
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));
        final Specky.SpecContext spec = parser.spec();
        final TypeResolver resolver = new TypeResolverBuilder().build(spec);
        final SpecBuilder specBuilder = new SpecBuilder(resolver);

        final SpecDesc specDesc = specBuilder.build(spec);

        return generator.generate(specDesc);
    }

    public void write(InputStream inputStream, String outputPath) throws IOException {
        new Writer(outputPath).write(generate(inputStream));
    }
}
