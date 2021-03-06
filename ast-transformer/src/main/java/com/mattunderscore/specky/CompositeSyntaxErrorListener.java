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

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.nio.file.Path;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;

import com.mattunderscore.specky.error.listeners.SyntaxErrorListener;

/**
 * Composite syntax error listener.
 * @author Matt Champion 27/01/2017
 */
public final class CompositeSyntaxErrorListener implements SyntaxErrorListener {
    private final Collection<SyntaxErrorListener> delegates;

    /**
     * Constructor.
     */
    private CompositeSyntaxErrorListener(Collection<SyntaxErrorListener> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void syntaxError(
            Path path,
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        delegates.forEach(delegate -> delegate
            .syntaxError(path, recognizer, offendingSymbol, line, charPositionInLine, msg, e));
    }

    /**
     * Compose multiple listeners together.
     */
    public static SyntaxErrorListener composeSyntaxListeners(SyntaxErrorListener... listeners) {
        return new CompositeSyntaxErrorListener(asList(copyOf(listeners, listeners.length)));
    }
}
