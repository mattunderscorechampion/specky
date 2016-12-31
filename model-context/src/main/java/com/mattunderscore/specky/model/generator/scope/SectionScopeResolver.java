/* Copyright © 2016 Matthew Champion All rights reserved.

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

package com.mattunderscore.specky.model.generator.scope;

import java.util.HashMap;
import java.util.Map;

import com.mattunderscore.specky.SemanticErrorListener;
import com.mattunderscore.specky.licence.resolver.LicenceResolverImpl;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.MutableValueResolverImpl;

import net.jcip.annotations.NotThreadSafe;

/**
 * Resolver for the scope to use.
 *
 * @author Matt Champion on 24/12/2016
 */
@NotThreadSafe
public final class SectionScopeResolver implements SectionScopeBuilder {
    private final SemanticErrorListener semanticErrorListener;
    private final TypeResolver typeResolver;
    private final Map<String, Scope> scopes = new HashMap<>();
    private Scope defaultScope;
    private PendingScope pendingScope;

    /**
     * Constructor.
     */
    public SectionScopeResolver(SemanticErrorListener semanticErrorListener, TypeResolver typeResolver) {
        this.semanticErrorListener = semanticErrorListener;
        this.typeResolver = typeResolver;
    }

    @Override
    public PendingScope beginNewScope(String sectionName) {
        final PendingScope scope = new PendingScopeImpl(
            sectionName,
            new MutableValueResolverImpl(),
            new SpecTypeResolver(),
            typeResolver,
            new LicenceResolverImpl(semanticErrorListener));
        pendingScope = scope;
        return scope;
    }

    @Override
    public void completeScope() {
        final Scope scope = pendingScope.toScope();
        final String sectionName = pendingScope.getSectionName();
        if (sectionName == null) {
            defaultScope = scope;
        }
        else {
            scopes.put(sectionName, scope);
        }
    }

    /**
     * Resolve a scope.
     */
    public Scope resolve(String sectionName) {
        return scopes.get(sectionName);
    }

    /**
     * Resolve the default scope.
     */
    public Scope resolve() {
        return defaultScope;
    }

    @Override
    public PendingScope currentScope() {
        return pendingScope;
    }
}
