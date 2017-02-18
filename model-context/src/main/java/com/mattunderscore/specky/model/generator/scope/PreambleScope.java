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

package com.mattunderscore.specky.model.generator.scope;

import java.util.Optional;

import com.mattunderscore.specky.type.resolver.JavaStandardTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.CompositeValueResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;
import com.mattunderscore.specky.value.resolver.JavaStandardDefaultValueResolver;
import com.mattunderscore.specky.value.resolver.NullValueResolver;
import com.mattunderscore.specky.value.resolver.OptionalValueResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Preamble scope. Contains declarations provided by the Java Runtime.
 *
 * @author Matt Champion 13/02/2017
 */
public final class PreambleScope extends AbstractScope {
    /**
     * Instance of the preamble scope.
     */
    public static final Scope INSTANCE = new PreambleScope();

    private final TypeResolver typeResolver = new JavaStandardTypeResolver();
    private final DefaultValueResolver valueResolver = new CompositeValueResolver()
        .with(new OptionalValueResolver())
        .with(new JavaStandardDefaultValueResolver())
        .with(new NullValueResolver());

    private PreambleScope() {
    }

    @Override
    public Optional<String> resolveType(String name) {
        return typeResolver.resolveType(name);
    }

    @Override
    public Optional<CodeBlock> resolveValue(String resolvedType, boolean optional) {
        return valueResolver.resolveValue(resolvedType, optional);
    }
}
