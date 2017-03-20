/* Copyright © 2017 Matthew Champion All rights reserved.

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
            return toValue(multiline);
        }
        else {
            return toValue(stringValue.STRING_LITERAL());
        }
    }

    /**
     * @return the unpacked string
     */
    static String toValue(TerminalNode node) {
        if (node == null) {
            return null;
        }
        else if (node.getSymbol().getType() == Specky.STRING_LITERAL) {
            final String literal = node.getText();
            return literal.substring(1, literal.length() - 1);
        }
        else if (node.getSymbol().getType() == Specky.MULTILINE_STRING_LITERAL) {
            final String literal = node.getText();
            return literal.substring(3, literal.length() - 3);
        }
        else {
            throw new IllegalArgumentException("Node cannot be made into a string." + node);
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
