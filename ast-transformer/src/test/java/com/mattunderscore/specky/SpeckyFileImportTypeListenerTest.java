package com.mattunderscore.specky;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;

/**
 * Unit tests for {@link SpeckyFileImportTypeListener}.
 *
 * @author Matt Champion on 24/12/16
 */
public final class SpeckyFileImportTypeListenerTest {
    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(SpeckyFileImportTypeListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("Test.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final SpecTypeResolver typeResolver = new SpecTypeResolver();
        final SpeckyFileImportTypeListener listener = new SpeckyFileImportTypeListener(typeResolver);
        parser.addParseListener(listener);

        parser.spec();

        // Verify types
        final Optional<String> i = typeResolver.resolve("Import");
        assertEquals(i.get(), "com.example.Import");
        final Optional<String> v = typeResolver.resolve("Value");
        assertEquals(v.get(), "com.example.Value");
    }
}
