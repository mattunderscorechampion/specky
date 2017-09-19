package com.mattunderscore.specky.generator;

import static com.mattunderscore.specky.model.ConstructionMethod.CONSTRUCTOR;
import static com.squareup.javapoet.ClassName.bestGuess;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mattunderscore.specky.literal.model.ComplexLiteral;
import com.mattunderscore.specky.literal.model.ConstantLiteral;
import com.mattunderscore.specky.literal.model.IntegerLiteral;
import com.mattunderscore.specky.literal.model.NamedComplexLiteral;
import com.mattunderscore.specky.literal.model.RealLiteral;
import com.mattunderscore.specky.literal.model.StringLiteral;
import com.mattunderscore.specky.literal.model.UnstructuredLiteral;
import com.mattunderscore.specky.model.ConstructionMethod;
import com.squareup.javapoet.CodeBlock;

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

    @Test
    public void generateConstant() throws Exception {
        final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

        final CodeBlock block = literalValueGenerator.generate(
            ConstantLiteral
                .builder()
                .typeName("java.math.BigInteger")
                .constant("ZERO")
                .build());

        assertEquals(CodeBlock.builder().add("$T.$N", bestGuess("java.math.BigInteger"), "ZERO").build(), block);
    }

    @Test
    public void generateFromComplexLiteral() throws Exception {
        final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

        final CodeBlock block = literalValueGenerator.generate(
            ComplexLiteral
                .builder()
                .typeName("com.example.Ex")
                .addSubvalue(IntegerLiteral
                    .builder()
                    .integerLiteral("5")
                    .build())
                .build());

        assertEquals(CodeBlock.builder().add("new $T($L)", bestGuess("com.example.Ex"), 5).build(), block);
    }

    @Test
    public void generateConstructorFromNamedComplexLiteral() throws Exception {
        final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

        final CodeBlock block = literalValueGenerator.generate(
            NamedComplexLiteral
                .builder()
                .typeName("com.example.Ex")
                .addName("first")
                .addSubvalue(IntegerLiteral
                    .builder()
                    .integerLiteral("5")
                    .build())
                .constructionMethod(CONSTRUCTOR)
                .build());

        assertEquals(CodeBlock.builder().add("new $T($L)", bestGuess("com.example.Ex"), 5).build(), block);
    }

    @Test
    public void generateBuilderFromNamedComplexLiteral() throws Exception {
        final LiteralValueGenerator literalValueGenerator = new LiteralValueGenerator();

        final CodeBlock block = literalValueGenerator.generate(
            NamedComplexLiteral
                .builder()
                .typeName("com.example.Ex")
                .addName("first")
                .addSubvalue(IntegerLiteral
                    .builder()
                    .integerLiteral("5")
                    .build())
                .build());

        assertEquals(CodeBlock.builder().add("$T.builder().first($L).build()", bestGuess("com.example.Ex"), 5).build(), block);
    }
}
