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

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.squareup.javapoet.CodeBlock;

/**
 * Implementation of {@link MutableValueResolver}.
 * @author Matt Champion on 30/07/2016
 */
public final class MutableValueResolverImpl implements MutableValueResolver {
    private final ConcurrentMap<String, CodeBlock> typeToValue = new ConcurrentHashMap<>(10, 0.5f, 2);

    @Override
    public Optional<CodeBlock> resolve(String resolvedType, boolean optional) {
        return Optional.ofNullable(typeToValue.get(resolvedType));
    }

    @Override
    public CompletableFuture<Void> register(String type, CodeBlock defaultValue) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(defaultValue);
        final CodeBlock currentValue = typeToValue.putIfAbsent(type, defaultValue);

        final CompletableFuture<Void> result = new CompletableFuture<>();

        if (currentValue != null && !currentValue.equals(defaultValue)) {
            result.completeExceptionally(
                new IllegalArgumentException("The type " + type + " is already registered to " + currentValue));
        }
        else {
            result.complete(null);
        }

        return result;
    }
}
