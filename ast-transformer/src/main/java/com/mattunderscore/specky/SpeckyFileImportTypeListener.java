package com.mattunderscore.specky;

import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;

/**
 * DSL AST listener for imported types.
 *
 * @author Matt Champion on 24/12/16
 */
public final class SpeckyFileImportTypeListener extends SpeckyBaseListener {
    private final SpecTypeResolver typeResolver;

    /**
     * Constructor.
     */
    public SpeckyFileImportTypeListener(SpecTypeResolver typeResolver) {

        this.typeResolver = typeResolver;
    }

    @Override
    public void exitSingleImport(Specky.SingleImportContext ctx) {
        final String importTypeName = ctx.qualifiedName().getText();
        final int lastPart = importTypeName.lastIndexOf('.');
        final String packageName = importTypeName.substring(0, lastPart);
        final String typeName = importTypeName.substring(lastPart + 1);

        typeResolver.registerTypeName(packageName, typeName);
    }
}
