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
import static java.util.Arrays.copyOf;

import java.nio.file.Path;
import java.util.Collection;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * A composite {@link SemanticErrorListener} that delegates to many others.
 *
 * @author Matt Champion 24/01/2017
 */
public final class CompositeSemanticErrorListener implements SemanticErrorListener {
    private final Collection<SemanticErrorListener> delegates;

    /**
     * Constructor.
     */
    private CompositeSemanticErrorListener(Collection<SemanticErrorListener> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void onSemanticError(Path file, String message, ParserRuleContext ruleContext) {
        delegates.forEach(delegate -> delegate.onSemanticError(file, message, ruleContext));
    }

    /**
     * Compose multiple listeners together.
     */
    public static SemanticErrorListener composeListeners(SemanticErrorListener... listeners) {
        return new CompositeSemanticErrorListener(asList(copyOf(listeners, listeners.length)));
    }
}
