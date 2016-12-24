package com.mattunderscore.specky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
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

import com.mattunderscore.specky.dsl.model.DSLLicence;
import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Unit tests for {@link SpeckyFileLicenceListener}.
 *
 * @author Matt Champion on 24/12/16
 */
public final class SpeckyFileLicenceListenerTest {
    @Mock
    private SemanticErrorListener errorListener;

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
        final CharStream stream = new ANTLRInputStream(SpeckyFileLicenceListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("Test.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final LicenceResolver licenceResolver = new LicenceResolver(errorListener);
        final SpeckyFileLicenceListener listener = new SpeckyFileLicenceListener(licenceResolver);
        parser.addParseListener(listener);

        parser.spec();

        // Verify licences
        final Optional<String> defaultLicence = licenceResolver.resolve(null);
        assertEquals(defaultLicence.get(), "default licence");
        final Optional<String> namedLicence = licenceResolver.resolve(DSLLicence.builder().identifier("n").build());
        assertEquals(namedLicence.get(), "named licence");
        assertFalse(licenceResolver.resolve(DSLLicence.builder().identifier("u").build()).isPresent());
        verify(errorListener).onSemanticError("An unknown name u was used to reference a licence");
    }
}
