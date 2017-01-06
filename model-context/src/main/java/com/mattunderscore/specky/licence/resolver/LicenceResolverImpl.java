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

package com.mattunderscore.specky.licence.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mattunderscore.specky.SemanticErrorListener;
import com.mattunderscore.specky.dsl.model.DSLLicence;

/**
 * Implementation of {@link LicenceResolver}.
 *
 * @author Matt Champion on 20/08/2016
 */
public final class LicenceResolverImpl implements LicenceResolver {
    private final Map<String, String> licences = new HashMap<>();
    private final SemanticErrorListener semanticErrorListener;
    private String defaultLicence;

    /**
     * Constructor.
     */
    public LicenceResolverImpl(SemanticErrorListener semanticErrorListener) {
        this.semanticErrorListener = semanticErrorListener;
    }

    @Override
    public LicenceResolver register(String licence) {
        if (defaultLicence != null) {
            semanticErrorListener.onSemanticError("Multiple default licences are not allowed");
        }
        defaultLicence = licence;
        return this;
    }

    @Override
    public LicenceResolver register(String name, String licence) {
        licences.put(name, licence);
        return this;
    }

    @Override
    public Optional<String> resolve(DSLLicence dslLicence) {
        if (dslLicence == null) {
            return Optional.ofNullable(defaultLicence);
        }

        final String inlineLicence = dslLicence.getLicence();
        if (inlineLicence != null) {
            return Optional.of(inlineLicence);
        }

        final String resolvedLicence = licences.get(dslLicence.getIdentifier());
        if (resolvedLicence != null) {
            return Optional.of(resolvedLicence);
        }

        semanticErrorListener.onSemanticError(
            "An unknown name " +
            dslLicence.getIdentifier() +
            " was used to reference a licence");

        return Optional.empty();
    }
}