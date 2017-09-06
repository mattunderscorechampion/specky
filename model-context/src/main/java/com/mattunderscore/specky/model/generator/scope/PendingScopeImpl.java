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

import com.mattunderscore.specky.licence.resolver.MutableLicenceResolver;
import com.mattunderscore.specky.type.resolver.MutableTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;

import net.jcip.annotations.NotThreadSafe;

import java.nio.file.Path;

/**
 * Implementation of {@link PendingScope}.
 *
 * @author Matt Champion on 24/12/2016
 */
@NotThreadSafe
public final class PendingScopeImpl implements PendingScope {
    private final String sectionName;
    private final MutableValueResolver valueResolver;
    private final MutableTypeResolver mutableTypeResolver;
    private final TypeResolver typeResolver;
    private final MutableLicenceResolver licenceResolver;
    private String author;
    private String packageName;
    private String copyrightHolder;
    private Path file;

    /**
     * Constructor.
     */
    /*package*/ PendingScopeImpl(
        String sectionName,
        MutableValueResolver valueResolver,
        MutableTypeResolver mutableTypeResolver,
        TypeResolver typeResolver,
        MutableLicenceResolver licenceResolver,
        Path file) {

        this.sectionName = sectionName;
        this.valueResolver = valueResolver;
        this.mutableTypeResolver = mutableTypeResolver;
        this.typeResolver = typeResolver;
        this.licenceResolver = licenceResolver;
        this.file = file;
    }

    @Override
    public String getSectionName() {
        return sectionName;
    }

    @Override
    public MutableValueResolver getValueResolver() {
        return valueResolver;
    }

    @Override
    public MutableTypeResolver getImportTypeResolver() {
        return mutableTypeResolver;
    }

    @Override
    public MutableLicenceResolver getLicenceResolver() {
        return licenceResolver;
    }

    @Override
    public Scope toScope(Scope parentScope) {
        final TypeResolver resolver = new TypeResolverBuilder()
            .registerResolver(mutableTypeResolver)
            .registerResolver(typeResolver)
            .build();

        return new ScopeImpl(
            parentScope == null ? PreambleScope.INSTANCE : parentScope,
            valueResolver,
            resolver,
            licenceResolver,
            author,
            packageName,
            copyrightHolder,
            file);
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void setCopyrightHolder(String copyrightHolder) {
        this.copyrightHolder = copyrightHolder;
    }
}
