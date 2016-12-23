package com.mattunderscore.specky;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import com.mattunderscore.specky.dsl.model.DSLImportDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;

/**
 * Unit tests for {@link SpeckyFileScopeListener}.
 *
 * @author Matt Champion on 23/12/16
 */
public final class SpeckyFileScopeListenerTest {
    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(SpeckyFileScopeListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("Test.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final SpeckyFileScopeListener listener = new SpeckyFileScopeListener();
        parser.addParseListener(listener);

        parser.spec();

        final List<DSLImportDesc> imports = listener.getImports();

        assertThat(
            imports,
            containsInAnyOrder(
                DSLImportDesc
                    .builder()
                    .typeName("com.example.Import")
                    .build(),
                DSLImportDesc
                    .builder()
                    .typeName("com.example.Value")
                    .defaultValue("\"x\"")
                    .build()
            ));
    }
}
