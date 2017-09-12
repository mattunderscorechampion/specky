/* Copyright © 2017 Matthew Champion
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

import com.mattunderscore.specky.construction.method.resolver.MutableConstructionMethodResolver;
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;
import net.jcip.annotations.NotThreadSafe;

/**
 * DSL AST listener for construction methods.
 *
 * @author Matt Champion 12/09/2017
 */
@NotThreadSafe
public final class FileConstructionMethodListener extends SpeckyBaseListener {
    private final InternalSemanticErrorListener errorListener;
    private final MutableConstructionMethodResolver constructionMethodResolver;
    private String packageName;
    private ConstructionMethod constructionMethod;

    /**
     * Constructor.
     */
    public FileConstructionMethodListener(
            InternalSemanticErrorListener errorListener,
            MutableConstructionMethodResolver constructionMethodResolver) {

        this.errorListener = errorListener;
        this.constructionMethodResolver = constructionMethodResolver;
    }

    @Override
    public void exitPackage_name(Specky.Package_nameContext ctx) {
        packageName = ctx.qualifiedName().getText();
    }

    @Override
    public void exitOpts(Specky.OptsContext ctx) {
        final Specky.ConstructionContext context = ctx.construction();
        if (context.CONSTRUCTOR() != null) {
            constructionMethod = ConstructionMethod.CONSTRUCTOR;
        }
        else if (context.IMMUTABLE_BUILDER() != null) {
            constructionMethod = ConstructionMethod.IMMUTABLE_BUILDER;
        }
        else if (context.MUTABLE_BUILDER() != null) {
            constructionMethod = ConstructionMethod.MUTABLE_BUILDER;
        }
        else if (context.FROM_DEFAULTS() != null) {
            constructionMethod = ConstructionMethod.FROM_DEFAULTS;
        }
        else {
            constructionMethod = ConstructionMethod.CONSTRUCTOR;
        }
    }

    @Override
    public void enterImplementationSpec(Specky.ImplementationSpecContext ctx) {
        constructionMethod = ConstructionMethod.CONSTRUCTOR;
    }

    @Override
    public void exitImplementationSpec(Specky.ImplementationSpecContext ctx) {
        constructionMethodResolver
            .registerConstructionMethod(packageName + "." + ctx.Identifier().getText(), constructionMethod)
            .exceptionally(t -> {
                errorListener.onSemanticError(t.getMessage(), ctx);
                return null;
            });
    }
}
