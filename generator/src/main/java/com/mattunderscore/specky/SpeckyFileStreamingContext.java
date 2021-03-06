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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.antlr.v4.runtime.ANTLRInputStream;

import com.mattunderscore.specky.context.file.FileContext;

/**
 * @author Matt Champion 15/01/2017
 */
public final class SpeckyFileStreamingContext {
    private final List<Path> filesToParse = new ArrayList<>();
    private final AtomicBoolean consumed = new AtomicBoolean(false);
    private final ReportingFileErrorListener errorListener = new ReportingFileErrorListener(System.err);

    /**
     * Constructor.
     */
    public SpeckyFileStreamingContext() {
    }

    /**
     * Add path to parse.
     */
    public synchronized SpeckyFileStreamingContext addFileToParse(Path path) {
        filesToParse.add(path);
        return this;
    }

    /**
     * Parse files.
     * @throws IllegalStateException if has been called before
     */
    public synchronized SpeckyParsingContext open() throws IOException {
        if (consumed.compareAndSet(false, true)) {

            final List<FileContext> fileContexts = new ArrayList<>();
            for (final Path path : filesToParse) {
                final FileContext context = new FileContext();
                context.setFile(path);
                final InputStream input;
                try {
                    input = Files.newInputStream(path);
                }
                catch (IOException e) {
                    errorListener.onException(path, "The file cannot be opened", e);
                    continue;
                }

                try {
                    context.setAntlrStream(new ANTLRInputStream(input));
                    fileContexts.add(context);
                }
                catch (IOException e) {
                    errorListener.onException(path, "The file cannot be read", e);
                    try {
                        input.close();
                    }
                    catch (IOException e2) {
                        errorListener.onException(path, "The failed stream cannot be closed", e2);
                    }
                }
            }

            return new SpeckyParsingContext(fileContexts);
        }
        else {
            throw new IllegalStateException("Context has already been parsed");
        }
    }
}
