package com.mattunderscore.specky.error.listeners;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import java.nio.file.Paths;

import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link CountingSemanticErrorListener}.
 *
 * @author Matt Champion on 12/10/2016
 */
public final class CountingSemanticErrorListenerTest {
    @Mock
    private ParserRuleContext ctx;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void onSemanticError() {
        final CountingSemanticErrorListener listener = new CountingSemanticErrorListener();
        listener.onSemanticError(Paths.get("."), "Test error", ctx);
        assertEquals(1, listener.getErrorCount());
    }
}
