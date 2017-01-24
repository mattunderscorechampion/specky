package com.mattunderscore.specky;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.parser.Specky.String_valueContext;

/**
 * Parser utilities. Support working with the generated parser.
 *
 * @author Matt Champion 24/01/2017
 */
/*package*/ final class ParserUtils {
    private ParserUtils() {
    }

    /**
     * @return the unpacked string
     */
    static String toValue(String_valueContext stringValue) {
        if (stringValue == null) {
            return null;
        }

        final TerminalNode multiline = stringValue.MULTILINE_STRING_LITERAL();
        if (multiline != null) {
            final String literal = multiline.getText();
            return literal.substring(3, literal.length() - 3);
        }
        else {
            final String literal = stringValue.StringLiteral().getText();
            return literal.substring(1, literal.length() - 1);
        }
    }
}
