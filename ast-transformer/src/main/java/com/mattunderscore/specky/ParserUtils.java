package com.mattunderscore.specky;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.model.ConstructionMethod;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.Specky.OptsContext;
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

    /**
     * @return the construction method
     */
    static ConstructionMethod toConstructionDesc(OptsContext options) {
        if (options == null || options.construction() == null) {
            return ConstructionMethod.CONSTRUCTOR;
        }

        final String token = options.construction().getText();
        if ("constructor".equals(token)) {
            return ConstructionMethod.CONSTRUCTOR;
        }
        else if ("builder".equals(token)) {
            return ConstructionMethod.MUTABLE_BUILDER;
        }
        else if ("immutable builder".equals(token)) {
            return ConstructionMethod.IMMUTABLE_BUILDER;
        }
        else if ("from defaults".equals(token)) {
            return ConstructionMethod.FROM_DEFAULTS;
        }
        else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    /**
     * @return if with property methods should be generated
     */
    static boolean withModifications(Specky.OptsContext options) {
        return !(options == null || options.WITH_MODIFICATION() == null);
    }
}
