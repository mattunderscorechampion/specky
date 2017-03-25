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

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * Abstract scope that has a parent scope to delegate to if nothing is found.
 * @author Matt Champion 18/02/2017
 */
public abstract class AbstractChildScope extends AbstractScope {
    private final Scope parentScope;

    /**
     * Constructor.
     */
    /*package*/ AbstractChildScope(Scope parentScope) {
        this.parentScope = parentScope;
    }

    /**
     * @return the author in the local scope
     */
    protected Optional<String> getLocalAuthor() {
        return empty();
    }

    /**
     * @return the package in the local scope
     */
    protected Optional<String> getLocalPackage() {
        return empty();
    }

    /**
     * @return resolve the licence from the local scope
     */
    protected Optional<String> resolveLocalLicence(String name) {
        return empty();
    }

    /**
     * @return resolve the type from the local scope
     */
    protected Optional<String> resolveLocalType(String name) {
        return empty();
    }

    /**
     * @return resolve the default value from the local scope
     */
    protected Optional<LiteralDesc> resolveLocalValue(String resolvedType, boolean optional) {
        return empty();
    }

    /**
     * @return the copyright holder in the local scope
     */
    protected Optional<String> getLocalCopyrightHolder() {
        return empty();
    }

    @Override
    public final String getAuthor() {
        return getLocalAuthor().orElseGet(parentScope::getAuthor);
    }

    @Override
    public final String getPackage() {
        return getLocalPackage().orElseGet(parentScope::getPackage);
    }

    @Override
    public final String getCopyrightHolder() {
        return getLocalCopyrightHolder().orElseGet(parentScope::getCopyrightHolder);
    }

    @Override
    public final Optional<String> resolveLicence(String name) {
        final Optional<String> localLicence = resolveLocalLicence(name);
        if (localLicence.isPresent()) {
            return localLicence;
        }
        else {
            return parentScope.resolveLicence(name);
        }
    }

    @Override
    public final Optional<String> resolveType(String name) {
        final Optional<String> localType = resolveLocalType(name);
        if (localType.isPresent()) {
            return localType;
        }
        else {
            return parentScope.resolveType(name);
        }
    }

    @Override
    public final Optional<LiteralDesc> resolveValue(String resolvedType, boolean optional) {
        final Optional<LiteralDesc> localValue = resolveLocalValue(resolvedType, optional);
        if (localValue.isPresent()) {
            return localValue;
        }
        else {
            return parentScope.resolveValue(resolvedType, optional);
        }
    }

    @Override
    public final TemplateContext toTemplateContext(String typeName) {
        return TemplateContext
            .builder()
            .typeName(typeName)
            .author(getAuthor())
            .copyrightHolder(getCopyrightHolder())
            .build();
    }
}
