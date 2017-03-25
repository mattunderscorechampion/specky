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

import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.licence.resolver.LicenceResolverImpl;
import com.mattunderscore.specky.literal.model.LiteralDesc;
import com.mattunderscore.specky.type.resolver.JavaStandardTypeResolver;
import com.mattunderscore.specky.type.resolver.TypeResolver;
import com.mattunderscore.specky.value.resolver.CompositeValueResolver;
import com.mattunderscore.specky.value.resolver.DefaultValueResolver;
import com.mattunderscore.specky.value.resolver.JavaStandardDefaultValueResolver;
import com.mattunderscore.specky.value.resolver.NullValueResolver;
import com.mattunderscore.specky.value.resolver.OptionalValueResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Preamble scope. Contains declarations provided by the Java Runtime.
 *
 * @author Matt Champion 13/02/2017
 */
public final class PreambleScope extends AbstractScope {
    /**
     * Instance of the preamble scope.
     */
    public static final Scope INSTANCE = create();

    private final TypeResolver typeResolver = new JavaStandardTypeResolver();
    private final DefaultValueResolver valueResolver = new CompositeValueResolver()
        .with(new OptionalValueResolver())
        .with(new JavaStandardDefaultValueResolver())
        .with(new NullValueResolver());
    private final LicenceResolver licenceResolver;

    private PreambleScope(LicenceResolver licenceResolver) {
        this.licenceResolver = licenceResolver;
    }

    @Override
    public Optional<String> resolveType(String name) {
        return typeResolver.resolveType(name);
    }

    @Override
    public Optional<LiteralDesc> resolveValue(String resolvedType, boolean optional) {
        return valueResolver.resolveValue(resolvedType, optional);
    }

    @Override
    public Optional<String> resolveLicence(String name) {
        return licenceResolver.resolveLicence(name);
    }

    private static PreambleScope create() {
        final LicenceResolverImpl licenceResolver = new LicenceResolverImpl();

        final String bsd3 = loadAsString("com/mattunderscore/specky/bsd-3-clause.template");
        if (bsd3 != null) {
            licenceResolver.register("BSD-3-Clause", bsd3);
            licenceResolver.register("BSD3Clause", bsd3);
            licenceResolver.register("BSD3", bsd3);
        }
        final String bsd2 = loadAsString("com/mattunderscore/specky/bsd-2-clause.template");
        if (bsd2 != null) {
            licenceResolver.register("BSD-2-Clause", bsd2);
            licenceResolver.register("BSD2Clause", bsd2);
            licenceResolver.register("BSD2", bsd2);
        }
        final String mit = loadAsString("com/mattunderscore/specky/mit.template");
        if (mit != null) {
            licenceResolver.register("MIT", mit);
        }
        final String apache2 = loadAsString("com/mattunderscore/specky/apache-2.template");
        if (apache2 != null) {
            licenceResolver.register("Apache-2", apache2);
            licenceResolver.register("Apache2", apache2);
        }
        final String gpl3 = loadAsString("com/mattunderscore/specky/gpl-3.template");
        if (gpl3 != null) {
            licenceResolver.register("GPL-3", gpl3);
            licenceResolver.register("GPL3", gpl3);
        }
        final String gpl2 = loadAsString("com/mattunderscore/specky/gpl-2.template");
        if (gpl2 != null) {
            licenceResolver.register("GPL-2", gpl2);
            licenceResolver.register("GPL2", gpl2);
        }

        return new PreambleScope(licenceResolver);
    }

    private static String loadAsString(String resourceName) {
        final InputStream resource = PreambleScope
            .class
            .getClassLoader()
            .getResourceAsStream(resourceName);

        if (resource != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                while (true) {
                    final int value = resource.read();
                    if (value == -1) {
                        break;
                    }
                    baos.write(value);
                }
                return baos.toString("UTF-8");
            }
            catch (IOException ex) {
                return null;
            }
        }

        return null;
    }
}
