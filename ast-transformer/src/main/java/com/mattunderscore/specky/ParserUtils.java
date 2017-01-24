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

        if (options.construction().CONSTRUCTOR() != null) {
            return ConstructionMethod.CONSTRUCTOR;
        }
        else if (options.construction().MUTABLE_BUILDER() != null) {
            return ConstructionMethod.MUTABLE_BUILDER;
        }
        else if (options.construction().IMMUTABLE_BUILDER() != null) {
            return ConstructionMethod.IMMUTABLE_BUILDER;
        }
        else if (options.construction().FROM_DEFAULTS() != null) {
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
