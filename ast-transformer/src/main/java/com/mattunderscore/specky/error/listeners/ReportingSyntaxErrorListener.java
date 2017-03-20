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

package com.mattunderscore.specky.error.listeners;

import com.mattunderscore.specky.parser.SpeckyLexer;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.io.PrintStream;
import java.nio.file.Path;

import static java.util.stream.Collectors.joining;

/**
 * A {@link ANTLRErrorListener} for reporting syntax errors.
 *
 * @author Matt Champion 30/01/2017
 */
public final class ReportingSyntaxErrorListener implements SyntaxErrorListener {
    private final PrintStream ps;

    private ReportingSyntaxErrorListener(PrintStream ps) {
        this.ps = ps;
    }

    @Override
    public void syntaxError(
            Path path,
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        if (e instanceof LexerNoViableAltException) {
            tokenRecognition(path, line, charPositionInLine, (LexerNoViableAltException) e);
        }
        else if (e instanceof InputMismatchException) {
            unexpectedInput(path, (Token) offendingSymbol, line, charPositionInLine, (InputMismatchException) e, recognizer);
        }
        else {
            ps.printf("%s\nAt line %d, at column %d\n---\n", msg, line, charPositionInLine);
        }
    }

    private void tokenRecognition(Path path, int line, int charPositionInLine, LexerNoViableAltException e) {
        final String badToken = e.getInputStream().getText(Interval.of(e.getStartIndex(), e.getStartIndex()));
        if (badToken.startsWith("\"")) {
            ps.printf(
                "Found an unterminated string literal\n%s:%d:%d\n---\n",
                path,
                line,
                charPositionInLine);
        }
        else {
            ps.printf(
                "Encountered an unrecognised token\nSee: %s\n%s:%d:%d\n---\n",
                path,
                badToken,
                line,
                charPositionInLine);
        }
    }

    private void unexpectedInput(
            Path path, Token offendingSymbol,
            int line,
            int charPositionInLine,
            InputMismatchException e,
            Recognizer<?, ?> recognizer) {

        ps.printf(
            "Encountered an unexpected token\nEncountered %s\nExpecting one of %s\n%s:%d:%d\n---\n",
            getTokenName(offendingSymbol.getType(), recognizer),
            e
                .getExpectedTokens()
                .toList()
                .stream()
                .map(tokenType -> getTokenName(tokenType, recognizer))
                .distinct()
                .collect(joining(", ")),
            path,
            line,
            charPositionInLine);
    }

    private String getTokenName(int tokenType, Recognizer<?, ?> recognizer) {
        if (tokenType == Token.EOF) {
            return "end of file";
        }
        else if (tokenType == SpeckyLexer.STRING_LITERAL || tokenType == SpeckyLexer.MULTILINE_STRING_LITERAL) {
            return "string literal";
        }
        else {
            return recognizer.getVocabulary().getDisplayName(tokenType);
        }
    }

    /**
     * Report the errors to a print stream.
     */
    public static SyntaxErrorListener reportSyntaxErrorsTo(PrintStream ps) {
        return new ReportingSyntaxErrorListener(ps);
    }
}
