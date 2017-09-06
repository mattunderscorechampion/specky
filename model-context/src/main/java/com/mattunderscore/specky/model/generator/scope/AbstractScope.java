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

import com.mattunderscore.specky.context.file.TemplateContext;
import com.mattunderscore.specky.literal.model.LiteralDesc;

import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * Abstract {@link Scope} implementation.
 * @author Matt Champion 13/02/2017
 */
public abstract class AbstractScope implements Scope {
    @Override
    public Optional<String> resolveLicence(String name) {
        return empty();
    }

    @Override
    public Optional<String> resolveType(String name) {
        return empty();
    }

    @Override
    public Optional<LiteralDesc> resolveValue(String resolvedType, boolean optional) {
        return empty();
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getPackage() {
        return null;
    }

    @Override
    public String getCopyrightHolder() {
        return null;
    }

    @Override
    public Path getFile() {
        return null;
    }

    @Override
    public TemplateContext toTemplateContext(String typeName) {
        return TemplateContext
            .builder()
            .typeName(typeName)
            .build();
    }
}
