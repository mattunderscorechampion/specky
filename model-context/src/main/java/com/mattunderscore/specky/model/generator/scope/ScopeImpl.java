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

import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;

import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Implementation of {@link Scope}.
 * @author Matt Champion on 21/08/2016
 */
public final class ScopeImpl extends AbstractChildScope {
    private final DefaultValueResolver valueResolver;
    private final TypeResolver typeResolver;
    private final LicenceResolver licenceResolver;
    private final String author;
    private final String packageName;
    private final String copyrightHolder;
    private final Path file;

    /**
     * Constructor.
     */
    // CHECKSTYLE.OFF: ParameterNumber
    /*package*/ ScopeImpl(
    // CHECKSTYLE.ON: ParameterNumber
        Scope parentScope,
        DefaultValueResolver valueResolver,
        TypeResolver typeResolver,
        LicenceResolver licenceResolver,
        String author,
        String packageName,
        String copyrightHolder,
        Path file) {

        super(parentScope);

        this.valueResolver = valueResolver;
        this.typeResolver = typeResolver;
        this.licenceResolver = licenceResolver;
        this.author = author;
        this.packageName = packageName;
        this.copyrightHolder = copyrightHolder;
        this.file = file;
    }

    @Override
    protected Optional<String> getLocalAuthor() {
        return ofNullable(author);
    }

    @Override
    protected Optional<String> getLocalPackage() {
        return ofNullable(packageName);
    }

    @Override
    protected Optional<String> resolveLocalLicence(String name) {
        return ofNullable(licenceResolver.resolveLicence(name).orElse(null));
    }

    @Override
    protected Optional<String> resolveLocalType(String name) {
        return ofNullable(typeResolver.resolveType(name).orElse(null));
    }

    @Override
    protected Optional<LiteralDesc> resolveLocalValue(String resolvedType, boolean optional) {
        return ofNullable(valueResolver.resolveValue(resolvedType, optional).orElse(null));
    }

    @Override
    protected Optional<String> getLocalCopyrightHolder() {
        return ofNullable(copyrightHolder);
    }

    @Override
    public Path getFile() {
        return file;
    }
}
