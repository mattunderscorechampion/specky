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

import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.type.resolver.JavaStandardTypeResolver;
import com.mattunderscore.specky.type.resolver.MutableTypeResolver;
import com.mattunderscore.specky.type.resolver.PropertyTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolverBuilder;
import com.mattunderscore.specky.value.resolver.CompositeValueResolver;
import com.mattunderscore.specky.value.resolver.JavaStandardDefaultValueResolver;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;
import com.mattunderscore.specky.value.resolver.NullValueResolver;
import com.mattunderscore.specky.value.resolver.OptionalValueResolver;

import net.jcip.annotations.NotThreadSafe;

/**
 * Implementation of {@link PendingScope}.
 *
 * @author Matt Champion on 24/12/2016
 */
@NotThreadSafe
public final class PendingScopeImpl implements PendingScope {
    private final String sectionName;
    private final MutableValueResolver valueResolver;
    private final MutableTypeResolver typeResolver;
    private final LicenceResolver licenceResolver;

    /**
     * Constructor.
     */
    /*package*/ PendingScopeImpl(
        String sectionName,
        MutableValueResolver valueResolver,
        MutableTypeResolver typeResolver,
        LicenceResolver licenceResolver) {
        this.sectionName = sectionName;

        this.valueResolver = valueResolver;
        this.typeResolver = typeResolver;
        this.licenceResolver = licenceResolver;
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
        return typeResolver;
    }

    @Override
    public LicenceResolver getLicenceResolver() {
        return licenceResolver;
    }

    @Override
    public Scope toScope() {
        final CompositeValueResolver compositeValueResolver = new CompositeValueResolver()
            .with(new OptionalValueResolver())
            .with(new JavaStandardDefaultValueResolver())
            .with(valueResolver)
            .with(new NullValueResolver());

        final TypeResolver resolver = new TypeResolverBuilder()
            .registerResolver(new JavaStandardTypeResolver())
            .registerResolver(typeResolver)
            .build();

        final PropertyTypeResolver propertyTypeResolver = new PropertyTypeResolver(resolver);

        return new ScopeImpl(compositeValueResolver, resolver, propertyTypeResolver, licenceResolver);
    }
}
