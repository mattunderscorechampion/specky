package com.mattunderscore.specky;

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

        assertTrue(listener.hasErrors());
    }

    @Test
    public void reportAmbiguity() {
        final ErrorCountingListener listener = new ErrorCountingListener();

        listener.reportAmbiguity(null, null, 0, 0, false, null, null);

        assertTrue(listener.hasErrors());
    }

    @Test
    public void reportAttemptingFullContext() {
        final ErrorCountingListener listener = new ErrorCountingListener();

        listener.reportAttemptingFullContext(null, null, 0, 0, null, null);

        assertFalse(listener.hasErrors());
    }

    @Test
    public void reportContextSensitivity() {
        final ErrorCountingListener listener = new ErrorCountingListener();

        listener.reportContextSensitivity(null, null, 0, 0, 0, null);

        assertFalse(listener.hasErrors());
    }
}
