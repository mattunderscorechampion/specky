package com.mattunderscore.specky;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.junit.Test;

import com.mattunderscore.specky.dsl.model.DSLImportDesc;
import com.mattunderscore.specky.parser.Specky;
import com.mattunderscore.specky.parser.SpeckyLexer;
import com.mattunderscore.specky.type.resolver.SpecTypeResolver;
import com.mattunderscore.specky.value.resolver.MutableValueResolver;
import com.squareup.javapoet.CodeBlock;

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

        final SpecTypeResolver typeResolver = new SpecTypeResolver();
        final MutableValueResolver valueResolver = new MutableValueResolver();
        final SpeckyFileScopeListener listener = new SpeckyFileScopeListener(typeResolver, valueResolver);
        parser.addParseListener(listener);

        parser.spec();

        final Optional<String> i = typeResolver.resolve("Import");
        assertEquals(i.get(), "com.example.Import");
        final Optional<String> v = typeResolver.resolve("Value");
        assertEquals(v.get(), "com.example.Value");

        final Optional<CodeBlock> vdv = valueResolver.resolve(null, "com.example.Value");
        assertEquals(vdv.get(), CodeBlock.of("\"x\""));

        assertFalse(valueResolver.resolve(null, "com.example.Import").isPresent());
    }
}
