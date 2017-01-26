/* Copyright © 2016-2017 Matthew Champion All rights reserved.

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

import com.mattunderscore.specky.model.generator.scope.SectionScopeBuilder;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;

/**
 * DSL AST listener for imported types.
 *
 * @author Matt Champion on 24/12/16
 */
public final class SectionImportTypeListener extends SpeckyBaseListener {

    private final SemanticErrorListener errorListener;
    private final SectionScopeBuilder scopeResolver;

    /**
     * Constructor.
     */
    public SectionImportTypeListener(SemanticErrorListener errorListener, SectionScopeBuilder scopeResolver) {
        this.errorListener = errorListener;
        this.scopeResolver = scopeResolver;
    }

    @Override
    public void exitSingleImport(Specky.SingleImportContext ctx) {
        final String importTypeName = ctx.qualifiedName().getText();
        final int lastPart = importTypeName.lastIndexOf('.');
        final String packageName = importTypeName.substring(0, lastPart);
        final String typeName = importTypeName.substring(lastPart + 1);

        scopeResolver
            .currentScope()
            .getImportTypeResolver()
            .registerTypeName(packageName, typeName)
            .exceptionally(t -> {
                errorListener.onSemanticError(t.getMessage(), ctx);
                return null;
            });
    }
}
