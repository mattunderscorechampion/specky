/* Copyright © 2016-2017 Matthew Champion
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

import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Implementation of {@link Scope}.
 * @author Matt Champion on 21/08/2016
 */
public final class ScopeImpl implements Scope {
    private final Scope parentScope;
    private final DefaultValueResolver valueResolver;
    private final TypeResolver typeResolver;
    private final LicenceResolver licenceResolver;
    private final String author;
    private final String packageName;

    /**
     * Constructor.
     */
    /*package*/ ScopeImpl(
        DefaultValueResolver valueResolver,
        TypeResolver typeResolver,
        LicenceResolver licenceResolver,
        String author,
        String packageName) {

        this(EmptyScope.INSTANCE, valueResolver, typeResolver, licenceResolver, author, packageName);
    }

    /**
     * Constructor.
     */
    /*package*/ ScopeImpl(
        Scope parentScope,
        DefaultValueResolver valueResolver,
        TypeResolver typeResolver,
        LicenceResolver licenceResolver,
        String author,
        String packageName) {

        this.parentScope = parentScope;
        this.valueResolver = valueResolver;
        this.typeResolver = typeResolver;
        this.licenceResolver = licenceResolver;
        this.author = author;
        this.packageName = packageName;
    }

    @Override
    public String getAuthor() {
        return ofNullable(author).orElseGet(parentScope::getAuthor);
    }

    @Override
    public String getPackage() {
        return ofNullable(packageName).orElseGet(parentScope::getPackage);
    }

    @Override
    public Optional<String> resolveLicence(String name) {
        return ofNullable(licenceResolver
            .resolveLicence(name)
            .orElseGet(() -> parentScope.resolveLicence(name).orElse(null)));
    }

    @Override
    public Optional<String> resolveType(String name) {
        return ofNullable(typeResolver
            .resolveType(name)
            .orElseGet(() -> parentScope.resolveType(name).orElse(null)));
    }

    @Override
    public Optional<CodeBlock> resolveValue(String resolvedType, boolean optional) {
        return ofNullable(valueResolver
            .resolveValue(resolvedType, optional)
            .orElseGet(() -> parentScope.resolveValue(resolvedType, optional).orElse(null)));
    }
}
