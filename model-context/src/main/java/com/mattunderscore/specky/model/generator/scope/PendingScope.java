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
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;

import net.jcip.annotations.NotThreadSafe;

/**
 * Pending scope.
 *
 * @author Matt Champion on 24/12/2016
 */
@NotThreadSafe
public final class PendingScope {
    private final String sectionName;
    private final MutableValueResolver valueResolver;
    private final SpecTypeResolver typeResolver;
    private final LicenceResolver licenceResolver;

    /**
     * Constructor.
     */
    /*package*/ PendingScope(
        String sectionName,
        MutableValueResolver valueResolver,
        SpecTypeResolver typeResolver,
        LicenceResolver licenceResolver) {
        this.sectionName = sectionName;

        this.valueResolver = valueResolver;
        this.typeResolver = typeResolver;
        this.licenceResolver = licenceResolver;
    }

    /**
     * @return the section name
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * @return the value resolver for the scope
     */
    public MutableValueResolver getValueResolver() {
        return valueResolver;
    }

    /**
     * @return the type resolver for types imported into the scope
     */
    public SpecTypeResolver getImportTypeResolver() {
        return typeResolver;
    }

    /**
     * @return the licence resolver for the scope
     */
    public LicenceResolver getLicenceResolver() {
        return licenceResolver;
    }

    /**
     * @return a scope
     */
    public Scope toScope() {
        return null;
    }
}
