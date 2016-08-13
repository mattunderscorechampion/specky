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

package com.mattunderscore.specky.value.resolver;

import com.mattunderscore.specky.dsl.model.DSLPropertyDesc;

import static java.util.Arrays.copyOf;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Composite value resolver.
 * @author Matt Champion on 23/06/2016
 */
public final class CompositeValueResolver implements DefaultValueResolver {
    private final DefaultValueResolver[] resolvers;

    /**
     * Constructor.
     */
    public CompositeValueResolver() {
        this(new DefaultValueResolver[0]);
    }

    private CompositeValueResolver(DefaultValueResolver[] resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Optional<String> resolve(DSLPropertyDesc propertyDesc, String resolvedType) {
        return Stream
            .of(resolvers)
            .map(resolver -> resolver.resolve(propertyDesc, resolvedType))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    /**
     * @return a new composite resolver with the provided one
     */
    public CompositeValueResolver with(DefaultValueResolver resolver) {
        final DefaultValueResolver[] newResolvers = copyOf(resolvers, resolvers.length + 1);
        newResolvers[resolvers.length] = resolver;
        return new CompositeValueResolver(newResolvers);
    }
}
