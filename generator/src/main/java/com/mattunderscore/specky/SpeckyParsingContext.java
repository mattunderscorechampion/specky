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

import static com.mattunderscore.specky.CompositeSemanticErrorListener.composeListeners;
import static com.mattunderscore.specky.ReportingSemanticErrorListener.reportTo;
import static java.util.Collections.singletonList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;

import com.mattunderscore.specky.model.SpecDesc;

/**
 * @author Matt Champion 15/01/2017
 */
public final class SpeckyParsingContext {
    private final AtomicBoolean consumed = new AtomicBoolean(false);
    private final List<InputStream> streamsToParse;

    /*package*/ SpeckyParsingContext(List<InputStream> streamsToParse) {
        this.streamsToParse = streamsToParse;
    }

    /**
     * Parse the input streams and return a generating context.
     * @throws IOException if there is a problem with the streams
     */
    public SpeckyGeneratingContext parse() throws IOException, ParsingError {
        if (consumed.compareAndSet(false, true)) {
            final CountingSemanticErrorListener errorCounter = new CountingSemanticErrorListener();
            final ModelGenerator generator =
                new ModelGenerator(composeListeners(errorCounter, reportTo(System.err)));
            final List<CharStream> streams = new ArrayList<>();
            for (final InputStream inputStream : streamsToParse) {
                try {
                    streams.add(new ANTLRInputStream(inputStream));
                }
                catch (IOException e) {
                    inputStream.close();
                }
            }

            @SuppressWarnings("PMD.PrematureDeclaration")
            final SpecDesc spec = generator.build(streams);

            final int errorCount = errorCounter.getErrorCount();
            if (errorCount > 0) {
                throw new ParsingError(errorCount);
            }

            return new SpeckyGeneratingContext(singletonList(spec));
        }
        else {
            throw new IllegalStateException("Context has already been parsed");
        }
    }
}
