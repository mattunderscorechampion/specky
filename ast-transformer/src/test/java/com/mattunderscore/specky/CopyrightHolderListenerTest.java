package com.mattunderscore.specky;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mattunderscore.specky.model.generator.scope.PendingScope;
import com.mattunderscore.specky.model.generator.scope.SectionScopeBuilder;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.MutableTypeResolver;

/**
 * Unit tests for {@link CopyrightHolderListener}.
 *
 * @author Matt Champion 26/02/2017
 */
public final class CopyrightHolderListenerTest {
    @Mock
    private SectionScopeBuilder sectionScopeBuilder;
    @Mock
    private PendingScope scope;
    @Mock
    private MutableTypeResolver typeResolver;

    @Before
    public void setUp() {
        initMocks(this);

        when(scope.getImportTypeResolver()).thenReturn(typeResolver);
        when(sectionScopeBuilder.currentScope()).thenReturn(scope);
    }

    @After
    public void postConditions() {
        verifyNoMoreInteractions(typeResolver, sectionScopeBuilder);
    }

    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(SectionImportTypeListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("SectionTest.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final CopyrightHolderListener listener = new CopyrightHolderListener(sectionScopeBuilder);
        parser.addParseListener(listener);

        parser.spec();

        verify(sectionScopeBuilder, times(1)).currentScope();
        verify(scope).setCopyrightHolder("Matthew Champion");
    }
}
