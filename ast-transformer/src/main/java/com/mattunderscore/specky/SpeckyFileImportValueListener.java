package com.mattunderscore.specky;

import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * DSL AST listener for imported default values.
 *
 * @author Matt Champion on 24/12/16
 */
public final class SpeckyFileImportValueListener extends SpeckyBaseListener {
    private final MutableValueResolver valueResolver;

    /**
     * Constructor.
     */
    public SpeckyFileImportValueListener(MutableValueResolver valueResolver) {

        this.valueResolver = valueResolver;
    }

    @Override
    public void exitSingleImport(Specky.SingleImportContext ctx) {

        if (ctx.default_value() != null) {
            valueResolver.register(
                ctx.qualifiedName().getText(),
                CodeBlock.of(ctx.default_value().ANYTHING().getText()));
        }
    }
}
