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

package com.mattunderscore.specky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

import com.mattunderscore.specky.construction.method.resolver.MutableConstructionMethodResolver;
import com.mattunderscore.specky.error.listeners.InternalSemanticErrorListener;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;

/**
 * Unit tests for {@link FileConstructionMethodListener}.
 *
 * @author Matt Champion 12/09/2017
 */
public final class FileConstructionMethodListenerTest {
    @Mock
    private InternalSemanticErrorListener errorListener;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(errorListener);
    }


    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(FileTypeListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("Test.spec"));

        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final MutableConstructionMethodResolver constructionMethodResolver = new MutableConstructionMethodResolver();
        final FileConstructionMethodListener listener = new FileConstructionMethodListener(errorListener, constructionMethodResolver);
        parser.addParseListener(listener);

        parser.spec();

        // Verify methods
        final Optional<ConstructionMethod> missing = constructionMethodResolver.resolveConstructionMethod("blah");
        assertFalse(missing.isPresent());

        final Optional<ConstructionMethod> first = constructionMethodResolver.resolveConstructionMethod(
            "com.example.FirstValue");
        assertEquals(ConstructionMethod.CONSTRUCTOR, first.get());

        final Optional<ConstructionMethod> second = constructionMethodResolver.resolveConstructionMethod(
            "com.example.SecondValue");
        assertEquals(ConstructionMethod.MUTABLE_BUILDER, second.get());
    }
}
