package com.mattunderscore.specky;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;

import java.util.BitSet;
import java.util.Collection;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

/**
 * Composite syntax error listener.
 * @author Matt Champion 27/01/2017
 */
public final class CompositeSyntaxErrorListener implements ANTLRErrorListener {
    private final Collection<ANTLRErrorListener> delegates;

    /**
     * Constructor.
     */
    private CompositeSyntaxErrorListener(Collection<ANTLRErrorListener> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        delegates.forEach(delegate -> delegate
            .syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
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

        delegates.forEach(delegate -> delegate
            .reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs));
    }

    @Override
    public void reportAttemptingFullContext(
            Parser recognizer,
            DFA dfa,
            int startIndex,
            int stopIndex,
            BitSet conflictingAlts,
            ATNConfigSet configs) {

        delegates.forEach(delegate -> delegate
            .reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs));
    }

    @Override
    public void reportContextSensitivity(
            Parser recognizer,
            DFA dfa,
            int startIndex,
            int stopIndex,
            int prediction,
            ATNConfigSet configs) {

        delegates.forEach(delegate -> delegate
            .reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs));
    }

    /**
     * Compose multiple listeners together.
     */
    public static ANTLRErrorListener composeSyntaxListeners(ANTLRErrorListener... listeners) {
        return new CompositeSyntaxErrorListener(asList(copyOf(listeners, listeners.length)));
    }
}
