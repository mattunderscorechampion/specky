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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;

import com.mattunderscore.specky.dsl.SpecBuilder;
import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;

/**
 * @author Matt Champion on 03/07/2016
 */
public final class SpeckyDSLParsingContext {
    private final AtomicBoolean consumed = new AtomicBoolean(false);
    private final List<InputStream> streamsToParse;

    /*package*/ SpeckyDSLParsingContext(List<InputStream> streamsToParse) {
        this.streamsToParse = streamsToParse;
    }

    /**
     * Parse the input streams and return a model generating context.
     * @throws IOException if there is a problem with the streams
     */
    public SpeckyModelGeneratingContext parse() throws IOException, ParsingError {
        if (consumed.compareAndSet(false, true)) {
            final List<Specky.SpecContext> specContexts = new ArrayList<>();
            final ErrorCountingListener errorCountingListener = new ErrorCountingListener();
            for (final InputStream inputStream : streamsToParse) {
                try {
                    final CharStream stream = new ANTLRInputStream(inputStream);
                    final SpeckyLexer lexer = new SpeckyLexer(stream);
                    final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));
                    parser.addErrorListener(errorCountingListener);
                    final Specky.SpecContext specContext = parser.spec();

                    specContexts.add(specContext);
                }
                finally {
                    inputStream.close();
                }
            }

            final int errorCount = errorCountingListener.getErrorCount();
            if (errorCount > 0) {
                throw new ParsingError(errorCount);
            }

            final List<DSLSpecDesc> specs = new ArrayList<>();
            for (final Specky.SpecContext specContext : specContexts) {
                final SpecBuilder specBuilder = new SpecBuilder();
                for (final DSLSpecDesc specDesc : specBuilder.build(specContext)) {
                    specs.add(specDesc);
                }
            }

            return new SpeckyModelGeneratingContext(specs);
        }
        else {
            throw new IllegalStateException("Context has already been parsed");
        }
    }
}
