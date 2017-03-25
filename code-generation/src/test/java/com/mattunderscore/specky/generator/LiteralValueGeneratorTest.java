package com.mattunderscore.specky.generator;

import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.RealLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
import com.squareup.javapoet.CodeBlock;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Matt Champion on 25/03/17
 */
public class LiteralValueGeneratorTest {
    @Test
    public void generateInt() throws Exception {
        final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

        final CodeBlock block = literalValueGenerator.generate(IntegerLiteral.builder().integerLiteral("5").build());

        assertEquals(CodeBlock.of("$L", 5), block);
    }

    @Test
    public void generateDouble() throws Exception {
        final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

        final CodeBlock block = literalValueGenerator.generate(RealLiteral.builder().realLiteral("5.0").build());

        assertEquals(CodeBlock.of("$L", 5.0), block);
    }

    @Test
    public void generateString() throws Exception {
        final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

        final CodeBlock block = literalValueGenerator.generate(StringLiteral.builder().stringLiteral("x").build());

        assertEquals(CodeBlock.of("$S", "x"), block);
    }

    @Test
    public void generateNull() throws Exception {
        final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

        final CodeBlock block = literalValueGenerator.generate(UnstructuredLiteral.builder().literal("null").build());

        assertEquals(CodeBlock.of("null"), block);
    }
}