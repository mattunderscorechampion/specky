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

package com.mattunderscore.specky.type.resolver;

import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * {@link TypeResolver} for specified types.
 *
 * @author Matt Champion on 08/06/16
 */
public final class SpecTypeResolver implements MutableTypeResolver {
    private final Map<String, String> specs = new HashMap<>();

    @Override
    public CompletableFuture<Void> registerTypeName(String packageName, String typeName) {
        final String fullyQualifiedTypeName = packageName + "." + typeName;
        final String presentFully = specs.putIfAbsent(fullyQualifiedTypeName, fullyQualifiedTypeName);
        final String presentShort = specs.putIfAbsent(typeName, fullyQualifiedTypeName);

        final CompletableFuture<Void> result = new CompletableFuture<>();

        if (presentFully != null || presentShort != null) {
            result.completeExceptionally(new IllegalArgumentException("The type cannot be registered"));
        }
        else {
            result.complete(null);
        }

        return result;
    }

    @Override
    public Optional<String> resolveType(String name) {
        return ofNullable(specs.get(name));
    }
}
