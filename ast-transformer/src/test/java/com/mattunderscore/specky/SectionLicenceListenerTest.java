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

package com.mattunderscore.specky;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mattunderscore.specky.licence.resolver.MutableLicenceResolver;
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.model.generator.scope.PendingScope;
import com.mattunderscore.specky.model.generator.scope.SectionScopeBuilder;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;

/**
 * Unit tests for {@link SectionLicenceListener}.
 *
 * @author Matt Champion 28/12/2016
 */
public final class SectionLicenceListenerTest {
    @Mock
    private SectionScopeBuilder sectionScopeBuilder;
    @Mock
    private PendingScope scope;
    @Mock
    private MutableLicenceResolver licenceResolver;
    @Mock
    private InternalSemanticErrorListener errorListener;

    @Before
    public void setUp() {
        initMocks(this);

        when(scope.getLicenceResolver()).thenReturn(licenceResolver);
        when(sectionScopeBuilder.currentScope()).thenReturn(scope);
        when(licenceResolver.resolveLicence(any())).thenReturn(Optional.of("licence"));
        when(licenceResolver.register(any())).thenReturn(completedFuture(null));
        when(licenceResolver.register(any(), any())).thenReturn(completedFuture(null));
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(licenceResolver, sectionScopeBuilder, errorListener);
    }

    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(SectionLicenceListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("SectionTest.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final SectionLicenceListener listener = new SectionLicenceListener(sectionScopeBuilder, errorListener);
        parser.addParseListener(listener);

        parser.spec();

        verify(sectionScopeBuilder, times(3)).currentScope();

        verify(licenceResolver).register("default licence");
        verify(licenceResolver).register("n", "named licence");
        verify(licenceResolver).register("default licence of named section");
    }
}
