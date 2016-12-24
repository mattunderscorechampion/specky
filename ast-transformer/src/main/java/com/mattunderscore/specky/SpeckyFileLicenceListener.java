package com.mattunderscore.specky;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;

/**
 * DSL AST listener for licences.
 *
 * @author Matt Champion on 24/12/16
 */
public final class SpeckyFileLicenceListener extends SpeckyBaseListener {
    private final LicenceResolver licenceResolver;

    /**
     * Constructor.
     */
    public SpeckyFileLicenceListener(LicenceResolver licenceResolver) {
        this.licenceResolver = licenceResolver;
    }

    @Override
    public void exitLicenceDeclaration(Specky.LicenceDeclarationContext ctx) {
        final String licenceText = toValue(ctx.string_value());

        if (ctx.Identifier() != null) {
            // Register a named licence
            final String licenceIdentifier = ctx.Identifier().getText();
            licenceResolver.register(licenceIdentifier, licenceText);
        }
        else {
            // Register the default licence
            licenceResolver.register(licenceText);
        }
    }

    private String toValue(Specky.String_valueContext stringValue) {
        if (stringValue == null) {
            return null;
        }

        final TerminalNode multiline = stringValue.MULTILINE_STRING_LITERAL();
        final String literal;
        final int trimLength;

        if (multiline != null) {
            literal = multiline.getText();
            trimLength = 3;
        }
        else {
            literal = stringValue.StringLiteral().getText();
            trimLength = 1;
        }

        return literal.substring(trimLength, literal.length() - trimLength);
    }
}
