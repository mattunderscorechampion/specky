package com.mattunderscore.specky;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import net.jcip.annotations.ThreadSafe;

/**
 * Syntax error listener. Counts the number of errors.
 * @author Matt Champion 27/01/2017
 */
@ThreadSafe
public final class CountingSyntaxErrorListener implements ANTLRErrorListener {
    private final AtomicInteger errorCount = new AtomicInteger(0);

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        errorCount.incrementAndGet();
    }

    @Override
    public void reportAmbiguity(
            Parser recognizer,
            DFA dfa,
            int startIndex,
            int stopIndex,
            boolean exact,
            BitSet ambigAlts,
            ATNConfigSet configs) {
    }

    @Override
    public void reportAttemptingFullContext(
            Parser recognizer,
            DFA dfa,
            int startIndex,
            int stopIndex,
            BitSet conflictingAlts,
            ATNConfigSet configs) {
    }

    @Override
    public void reportContextSensitivity(
            Parser recognizer,
            DFA dfa,
            int startIndex,
            int stopIndex,
            int prediction,
            ATNConfigSet configs) {
    }

    /**
     * @return the number of errors
     */
    public int getErrorCount() {
        return errorCount.get();
    }
}
