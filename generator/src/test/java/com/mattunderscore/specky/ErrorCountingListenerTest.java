package com.mattunderscore.specky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for {@link ErrorCountingListener}.
 *
 * @author Matt Champion on 02/10/2016
 */
public final class ErrorCountingListenerTest {

    @Test
    public void syntaxError() {
        final ErrorCountingListener listener = new ErrorCountingListener();

        listener.syntaxError(null, null, 0, 0, null, null);

        assertEquals(1, listener.getErrorCount());
    }

    @Test
    public void reportAmbiguity() {
        final ErrorCountingListener listener = new ErrorCountingListener();

        listener.reportAmbiguity(null, null, 0, 0, false, null, null);

        assertEquals(1, listener.getErrorCount());
    }

    @Test
    public void reportAttemptingFullContext() {
        final ErrorCountingListener listener = new ErrorCountingListener();

        listener.reportAttemptingFullContext(null, null, 0, 0, null, null);

        assertEquals(0, listener.getErrorCount());
    }

    @Test
    public void reportContextSensitivity() {
        final ErrorCountingListener listener = new ErrorCountingListener();

        listener.reportContextSensitivity(null, null, 0, 0, 0, null);

        assertEquals(0, listener.getErrorCount());
    }
}
