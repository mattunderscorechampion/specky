package com.mattunderscore.specky;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mattunderscore.specky.licence.resolver.LicenceResolver;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Processor for DSL AST listener.
 *
 * @author Matt Champion on 23/12/16
 */
public final class SpeckyFileScopeListener extends SpeckyBaseListener {
    private final SpecTypeResolver typeResolver;
    private final MutableValueResolver valueResolver;
    private final LicenceResolver licenceResolver;

    /**
     * Constructor.
     */
    public SpeckyFileScopeListener(
        SpecTypeResolver typeResolver,
        MutableValueResolver valueResolver,
        LicenceResolver licenceResolver) {

        this.typeResolver = typeResolver;
        this.valueResolver = valueResolver;
        this.licenceResolver = licenceResolver;
    }

    @Override
    public void exitSingleImport(Specky.SingleImportContext ctx) {
        final String importTypeName = ctx.qualifiedName().getText();
        final int lastPart = importTypeName.lastIndexOf('.');
        final String packageName = importTypeName.substring(0, lastPart);
        final String typeName = importTypeName.substring(lastPart + 1);

        typeResolver.registerTypeName(packageName, typeName);

        if (ctx.default_value() != null) {
            valueResolver.register(importTypeName, CodeBlock.of(ctx.default_value().ANYTHING().getText()));
        }
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
