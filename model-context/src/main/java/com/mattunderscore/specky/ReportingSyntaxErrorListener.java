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

import static java.util.stream.Collectors.joining;

import java.io.PrintStream;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import com.mattunderscore.specky.parser.SpeckyLexer;

/**
 * A {@link ANTLRErrorListener} for reporting syntax errors.
 *
 * @author Matt Champion 30/01/2017
 */
public final class ReportingSyntaxErrorListener extends BaseErrorListener {
    private final PrintStream ps;

    private ReportingSyntaxErrorListener(PrintStream ps) {
        this.ps = ps;
    }

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        if (e instanceof LexerNoViableAltException) {
            tokenRecognition(line, charPositionInLine, (LexerNoViableAltException) e);
        }
        else if (e instanceof InputMismatchException) {
            unexpectedInput((Token) offendingSymbol, line, charPositionInLine, (InputMismatchException) e, recognizer);
        }
        else {
            ps.printf("line %d:%d %s\n", line, charPositionInLine, msg);
        }
    }

    private void tokenRecognition(int line, int charPositionInLine, LexerNoViableAltException e) {
        final String badToken = e.getInputStream().getText(Interval.of(e.getStartIndex(), e.getStartIndex()));
        if (badToken.startsWith("\"")) {
            ps.printf("line %d:%d begins an unterminated string literal\n", line, charPositionInLine);
        }
        else {
            ps.printf("line %d:%d has an unrecognised token %s\n", line, charPositionInLine, badToken);
        }
    }

    private void unexpectedInput(
            Token offendingSymbol,
            int line,
            int charPositionInLine,
            InputMismatchException e,
            Recognizer<?, ?> recognizer) {

        ps.printf(
            "line %d:%d encountered %s was expecting one of %s\n",
            line,
            charPositionInLine,
            getTokenName(offendingSymbol.getType(), recognizer),
            e
                .getExpectedTokens()
                .toList()
                .stream()
                .map(tokenType -> getTokenName(tokenType, recognizer))
                .distinct()
                .collect(joining(", ")));
    }

    private String getTokenName(int tokenType, Recognizer<?, ?> recognizer) {
        if (tokenType == Token.EOF) {
            return "end of file";
        }
        else if (tokenType == SpeckyLexer.StringLiteral || tokenType == SpeckyLexer.MULTILINE_STRING_LITERAL) {
            return "string literal";
        }
        else {
            return recognizer.getVocabulary().getDisplayName(tokenType);
        }
    }

    /**
     * Report the errors to a print stream.
     */
    public static ANTLRErrorListener reportSyntaxErrorsTo(PrintStream ps) {
        return new ReportingSyntaxErrorListener(ps);
    }
}
