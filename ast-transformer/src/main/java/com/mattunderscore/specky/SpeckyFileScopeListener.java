package com.mattunderscore.specky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mattunderscore.specky.dsl.model.DSLImportDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyBaseListener;

/**
 * Processor for DSL AST listener.
 *
 * @author Matt Champion on 23/12/16
 */
public final class SpeckyFileScopeListener extends SpeckyBaseListener {
    private final List<DSLImportDesc> imports = new ArrayList<>();

    /**
     * Constructor.
     */
    public SpeckyFileScopeListener() {
    }

    @Override
    public void exitSingleImport(Specky.SingleImportContext ctx) {
        imports.add(
            DSLImportDesc
                .builder()
                .typeName(ctx.qualifiedName().getText())
                .ifThen(
                    ctx.default_value() != null,
                    builder -> builder.defaultValue(ctx.default_value().ANYTHING().getText()))
                .build());
    }

    /**
     * @return The imports
     */
    public List<DSLImportDesc> getImports() {
        return Collections.unmodifiableList(imports);
    }
}
