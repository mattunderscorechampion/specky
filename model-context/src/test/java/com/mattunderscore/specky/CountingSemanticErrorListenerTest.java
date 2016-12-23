package com.mattunderscore.specky;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link CountingSemanticErrorListener}.
 *
 * @author Matt Champion on 12/10/2016
 */
public final class CountingSemanticErrorListenerTest {

    @Test
    public void onSemanticError() {
        final CountingSemanticErrorListener listener = new CountingSemanticErrorListener();
        listener.onSemanticError("Test error");
        assertEquals(1, listener.getErrorCount());
    }
}
