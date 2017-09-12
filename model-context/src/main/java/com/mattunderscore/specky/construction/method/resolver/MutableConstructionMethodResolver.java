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

package com.mattunderscore.specky.construction.method.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.mattunderscore.specky.model.ConstructionMethod;
import net.jcip.annotations.NotThreadSafe;

/**
 * Mutable construction method resolver.
 *
 * @author Matt Champion 12/09/2017
 */
@NotThreadSafe
public final class MutableConstructionMethodResolver implements ConstructionMethodResolver {
    private final Map<String, ConstructionMethod> constructionMethods = new HashMap<>();

    @Override
    public Optional<ConstructionMethod> resolveConstructionMethod(String type) {
        return Optional.ofNullable(constructionMethods.get(type));
    }

    /**
     * Register a construction method.
     */
    public CompletableFuture<Void> registerConstructionMethod(
            String type,
            ConstructionMethod constructionMethod) {
        final ConstructionMethod present = constructionMethods.putIfAbsent(type, constructionMethod);
        if (present == null) {
            return CompletableFuture.completedFuture(null);
        }
        else {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("The construction method cannot be registered"));
            return future;
        }
    }
}
