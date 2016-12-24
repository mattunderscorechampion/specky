package com.mattunderscore.specky;

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

    /**
     * Constructor.
     */
    public SpeckyFileScopeListener(SpecTypeResolver typeResolver, MutableValueResolver valueResolver) {
        this.typeResolver = typeResolver;
        this.valueResolver = valueResolver;
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
}
