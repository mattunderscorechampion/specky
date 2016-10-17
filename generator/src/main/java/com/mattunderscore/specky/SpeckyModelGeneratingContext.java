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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mattunderscore.specky.dsl.model.DSLSpecDesc;
import com.mattunderscore.specky.model.SpecDesc;
import com.mattunderscore.specky.model.generator.ModelGenerator;
import com.mattunderscore.specky.model.generator.TypeDeriver;
import com.mattunderscore.specky.model.generator.scope.ScopeResolver;

/**
 * Generates a model from the DSL model.
 *
 * @author Matt Champion on 13/07/2016
 */
public final class SpeckyModelGeneratingContext {
    private final AtomicBoolean consumed = new AtomicBoolean(false);
    private final List<DSLSpecDesc> specs;
    private final CountingSemanticErrorListener semanticErrorListener;

    /*package*/ SpeckyModelGeneratingContext(List<DSLSpecDesc> specs) {
        semanticErrorListener = new CountingSemanticErrorListener();
        this.specs = specs;
    }

    /**
     * Generate the Java code.
     * @throws IllegalStateException if has been called before
     * @throws SemanticError if there is a problem with the semantics of the model
     */
    @SuppressWarnings("PMD.PrematureDeclaration")
    public SpeckyGeneratingContext generate() throws SemanticError {
        if (consumed.compareAndSet(false, true)) {
            final ScopeResolver scopeResolver = new ScopeResolver(semanticErrorListener)
                .createScopes(specs);

            final int errorCount0 = semanticErrorListener.getErrorCount();
            if (errorCount0 > 0) {
                throw new SemanticError(errorCount0 + " semantic errors reported");
            }

            final TypeDeriver typeDeriver = new TypeDeriver(scopeResolver, semanticErrorListener);

            final ModelGenerator modelGenerator = new ModelGenerator(
                specs,
                scopeResolver,
                typeDeriver,
                semanticErrorListener);

            final SpecDesc specDesc = modelGenerator.get();

            final int errorCount1 = semanticErrorListener.getErrorCount();
            if (errorCount1 > 0) {
                throw new SemanticError(errorCount1 + " semantic errors reported");
            }

            return new SpeckyGeneratingContext(specDesc);
        }
        else {
            throw new IllegalStateException("Context has already been generated");
        }
    }
}
