package com.mattunderscore.specky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;
import com.squareup.javapoet.CodeBlock;

/**
 * Unit tests for {@link SpeckyFileImportValueListener}.
 *
 * @author Matt Champion on 24/12/16
 */
public final class SpeckyFileImportValueListenerTest {
    @Test
    public void test() throws IOException {
        final CharStream stream = new ANTLRInputStream(SpeckyFileImportValueListenerTest
            .class
            .getClassLoader()
            .getResourceAsStream("Test.spec"));
        final SpeckyLexer lexer = new SpeckyLexer(stream);
        final Specky parser = new Specky(new UnbufferedTokenStream<CommonToken>(lexer));

        final MutableValueResolver valueResolver = new MutableValueResolver();
        final SpeckyFileImportValueListener listener = new SpeckyFileImportValueListener(valueResolver);
        parser.addParseListener(listener);

        parser.spec();

        // Verify default values
        final Optional<CodeBlock> vdv = valueResolver.resolve(null, "com.example.Value");
        assertEquals(vdv.get(), CodeBlock.of("\"x\""));
        assertFalse(valueResolver.resolve(null, "com.example.Import").isPresent());
    }
}
